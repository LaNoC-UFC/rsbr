package rbr;

public class RoutingPath {
	private String ip;
	private String dst;
	private String op;

	public RoutingPath(String ip, String dst, String op) {
		this.dst = dst;
		this.ip = ip;
		this.op = op;
	}

	public String getIp() {
		return ip;
	}

	public String getDst() {
		return dst;
	}

	public String getOp() {
		return op;
	}

	@Override
	public boolean equals(Object _that) {
		if(this == _that)
			return true;
		if(this.getClass() != _that.getClass())
			return false;
		RoutingPath that = (RoutingPath) _that;
		if(!this.getIp().equals(that.getIp()))
			return false;
		if(!this.getOp().equals(that.getOp()))
			return false;
		if(!this.getDst().equals(that.getDst()))
			return false;
		return true;
	}
}
