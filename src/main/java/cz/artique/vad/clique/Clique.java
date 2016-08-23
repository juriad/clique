package cz.artique.vad.clique;

import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.message.CallMessage;
import cz.artique.vad.network.message.JoinMessage;
import cz.artique.vad.network.message.LeaveMessage;
import cz.artique.vad.network.message.Message;
import cz.artique.vad.network.message.SearchMessage;
import cz.artique.vad.network.message.UnreachableMessage;
import cz.artique.vad.network.vertex.Behavior;
import cz.artique.vad.network.vertex.Subject;
import cz.artique.vad.network.vertex.VertexInterface;
import cz.artique.vad.network.vertex.VertexMode;

public class Clique implements Subject {
	private final VertexInterface vi;
	private final NavigableSet<String> neighbors;
	private final Set<String> unreachableNeighbors = new HashSet<String>();;

	private String left;
	private String right;
	private String current;

	public Clique(VertexInterface vi, Collection<String> neighbors) {
		this.vi = vi;
		this.neighbors = new ConcurrentSkipListSet<String>(neighbors);
		this.neighbors.add(vi.getId());

		left = this.neighbors.lower(vi.getId());
		right = this.neighbors.higher(vi.getId());
	}

	@Override
	public void onMessageReceived(Message message) {
		if (message instanceof CallMessage) {
			CallMessage call = (CallMessage) message;
			switch (call.getMethod()) {
			case "linearize":
				linearize(call.getArgs()[0]);
				break;
			case "inform-right":
				informRight(call.getArgs()[0], call.getArgs()[1]);
				break;
			case "inform-left":
				informLeft(call.getArgs()[0]);
				break;
			case "unreachable":
				unreachable(call.getArgs()[0]);
			}
		} else if (message instanceof JoinMessage) {
			join(((JoinMessage) message).getTo());
		} else if (message instanceof LeaveMessage) {
			leave();
		} else if (message instanceof SearchMessage) {
			search((SearchMessage) message);
		} else if (message instanceof UnreachableMessage) {
			String target = ((UnreachableMessage) message).getOriginal().getTarget();
			unreachable(target);
		}
	}

	@Override
	public void onTimeout() {
		String l = neighbors.lower(vi.getId());
		linearize(l);
		if (left != null) {
			vi.send(new CallMessage(left, "linearize", vi.getId()));
		}

		String r = neighbors.higher(vi.getId());
		linearize(r);
		if (right != null) {
			vi.send(new CallMessage(right, "linearize", vi.getId()));
		}

		if (left == null) {
			if (current == null) {
				current = getNext(null);
			}

			if (right != null) {
				String id2 = getNext(current);
				vi.send(new CallMessage(right, "inform-right", current, id2));
				current = id2;
			}
		} else {
			if (current != null) {
				if (!unreachableNeighbors.contains(current)) {
					neighbors.add(current);
				}
				current = null;
			}
		}
	}

	private void join(String to) {
		linearize(to);
	}

	private void leave() {
		vi.leave();
	}

	private void search(SearchMessage message) {
		String id = message.getId();
		if (id.equals(vi.getId())) {
			message.getCallback().onSuccess(vi);
		} else if (neighbors.contains(id)) {
			vi.send(new SearchMessage(id, id, message.getCallback()));
		} else {
			message.getCallback().onFailure(vi);
		}
	}

	private void unreachable(String target) {
		unreachableNeighbors.add(target);
		neighbors.remove(target);
		if (target.equals(left)) {
			left = null;
		}
		if (target.equals(right)) {
			right = null;
		}
	}

	private void linearize(String id) {
		if (id == null || unreachableNeighbors.contains(id)) {
			return;
		}
		neighbors.add(id);

		if (left != null) {
			if (id.compareTo(left) < 0) {
				vi.send(new CallMessage(left, "linearize", id));
			}
			if (id.compareTo(left) > 0 && id.compareTo(vi.getId()) < 0) {
				vi.send(new CallMessage(id, "linearize", left));
				left = id;
			}
		} else {
			if (id.compareTo(vi.getId()) < 0) {
				left = id;
			}
		}
		if (right != null) {
			if (id.compareTo(vi.getId()) > 0 && id.compareTo(right) < 0) {
				vi.send(new CallMessage(right, "linearize", right));
				right = id;
			}
			if (id.compareTo(right) > 0) {
				vi.send(new CallMessage(right, "linearize", id));
			}
		} else {
			if (id.compareTo(vi.getId()) > 0) {
				right = id;
			}
		}
	}

	private void informRight(String id1, String id2) {
		if (unreachableNeighbors.contains(id1)) {
			informUnreachable(id1, left);
			id1 = getNext(id1);

		} else {
			neighbors.add(id1);
		}
		if (unreachableNeighbors.contains(id2)) {
			informUnreachable(id2, left);
			id2 = getNext(id2);
		} else {
			neighbors.add(id2);
		}

		if (right != null) {
			vi.send(new CallMessage(right, "inform-right", id1, getNext(id1)));
		}

		while (!id1.equals(id2) && !(id1 = getNext(id1)).equals(id2)) {
			informLeft(id1);
			if (right != null) {
				vi.send(new CallMessage(right, "inform-right", id1, getNext(id1)));
			}
		}
	}

	private void informLeft(String id) {
		if (unreachableNeighbors.contains(id)) {
			informUnreachable(id, right);
			return;
		} else {
			neighbors.add(id);
		}

		if (left != null) {
			vi.send(new CallMessage(left, "inform-left", id));
		}
	}

	private void informUnreachable(String id, String to) {
		if (to == null) {
			return;
		}

		vi.send(new CallMessage(to, "unreachable", id));
	}

	private String getNext(String next) {
		if (next != null) {
			next = neighbors.higher(next);
		}
		if (next == null) {
			next = neighbors.first();
		}
		return next;
	}

	public static class CliqueBehavior implements Behavior<Clique> {
		private static final int NO_DETAILED_LOG_TRESHOLD = 100;

		@Override
		public Clique createSubject(VertexInterface si, Collection<String> neighbors) {
			return new Clique(si, neighbors);
		}

		@Override
		public String getType() {
			return "clique";
		}

		@Override
		public boolean isNetworkStabilized(Logger logger, Collection<Clique> subjects, int size) {
			Set<String> staying = new HashSet<String>();
			for (Clique c : subjects) {
				if (c.vi.getMode() == VertexMode.STAYING) {
					staying.add(c.vi.getId());
				}
			}

			int cnt = 0;
			int sum = 0;
			for (Clique c : subjects) {
				if (c.vi.getMode() != VertexMode.STAYING) {
					continue;
				}

				StringBuilder sb = new StringBuilder();
				int ns = 0;
				int nsAll = 0;
				for (String n : c.neighbors) {
					if (size < NO_DETAILED_LOG_TRESHOLD) {
						if (nsAll > 0) {
							sb.append(", ");
						}
						sb.append(n);
					}

					if (staying.contains(n)) {
						ns++;
					}
					nsAll++;
				}

				sum += ns;
				if (ns == size) {
					cnt++;
				}

				logger.log(c.vi.getId() + " (" + ns + "~" + nsAll + "): {" + sb + "}");
			}
			logger.log("Average degree: " + sum / (double) size + ", finished: " + cnt + ", size: " + size);
			return cnt == size;
		}
	}
}
