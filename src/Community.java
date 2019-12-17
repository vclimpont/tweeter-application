import java.util.ArrayList;
import java.util.Iterator;

public class Community {
	
	private ArrayList<LNode> lnodes;
	private int number;
	
	private int somme_in;
	private int somme_tot;
	
	public Community(int _number)
	{
		number = _number;
		lnodes = new ArrayList<LNode>();
	}
	
	public void initSums() {
		somme_in = 0;
		somme_tot = 0;
		for(LNode ln : lnodes)
		{
			int[] w = ln.getSumWeightLinkedToCommunity(number);
			somme_in += w[0];
			somme_tot += w[1];
		}
	}
	
	public void addLNode(LNode ln)
	{
		lnodes.add(ln);
	}
	
	public void removeLNode(LNode ln)
	{
		lnodes.remove(ln);
	}
	
	public void removeLNodeItr(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		ln.setCommunity(null);
	}

	public ArrayList<LNode> getLNodes() {
		return lnodes;
	}

	public void setLNodes(ArrayList<LNode> lnodes) {
		this.lnodes = lnodes;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSomme_in() {
		return somme_in;
	}

	public void setSomme_in(int somme_in) {
		this.somme_in = somme_in;
	}

	public int getSomme_tot() {
		return somme_tot;
	}

	public void setSomme_tot(int somme_tot) {
		this.somme_tot = somme_tot;
	}
	
}