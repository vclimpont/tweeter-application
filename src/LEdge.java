
public class LEdge {

	private LNode i;
	private LNode j;
	private int weight; 
	
	public LEdge(LNode _i, LNode _j, int w) 
	{
		i = _i;
		j = _j;
		weight = w;
	}

	public LNode getI() {
		return i;
	}

	public void setI(LNode i) {
		this.i = i;
	}

	public LNode getJ() {
		return j;
	}

	public void setJ(LNode j) {
		this.j = j;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	

}
