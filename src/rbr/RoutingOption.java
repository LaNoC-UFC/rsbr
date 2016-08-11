package rbr;

import util.Vertex;

class RoutingOption {
	private String ip;
	private Vertex dst;
	private String op;

	RoutingOption(String ip, Vertex dst, String op) {
		this.dst = dst;
		this.ip = ip;
		this.op = op;
	}

	public String getIp() {
		return ip;
	}

	Vertex destination() {
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
		RoutingOption that = (RoutingOption) _that;
		if(!this.getIp().equals(that.getIp()))
			return false;
		if(!this.getOp().equals(that.getOp()))
			return false;
		if(!this.destination().equals(that.destination()))
			return false;
		return true;
	}
}
