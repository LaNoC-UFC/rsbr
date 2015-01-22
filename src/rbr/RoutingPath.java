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

}
