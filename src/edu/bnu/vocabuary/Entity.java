package edu.bnu.vocabuary;

public class Entity {
	
	private String entity;
	private double prob;
	private int count;
	
	public Entity(String _name){
		entity=_name;
		prob=0;
		count=0;
	}
	
	public void increaseCount(){
		count++;
	}
	
	public void updateProb(int total){
		if(count==0)prob=0;
		else prob=(double)count/(double)total;
	}

	public String getName() {
		return entity;
	}

	public double getProb() {
		return prob;
	}

	public int getCount() {
		return count;
	}
	
	public String toString(){
		return entity+"\t"+count+"\t"+prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(((Entity)obj).getName().equals(this.entity))return true;
		else return false;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	
	

}
