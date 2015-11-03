package edu.bnu.vocabuary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Entity implements Comparable<Entity> {
	
	private String entity;
	private double prob;
	private Integer count;
	
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

	public Integer getCount() {
		return count;
	}
	
	public String toString(){
		return entity+","+count+","+prob;
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
	
	
	public int compareTo(Entity en) {
	
//        return this.getOrder().compareTo(arg0.getOrder());
        return this.getCount().compareTo(en.getCount());
    }
	
	public static void main(String[] args) {

		ArrayList<Entity> list=new ArrayList<Entity>();
		Entity e1=new Entity("n1");
		e1.setCount(1);
		e1.setProb(0.0);
		
		Entity e2=new Entity("n2");
		e2.setCount(2);
		e2.setProb(0.0);
		
		Entity e3=new Entity("n3");
		e3.setCount(3);
		e3.setProb(0.0);
		
		Entity e4=new Entity("n4");
		e4.setCount(4);
		e4.setProb(0.0);
		
		list.add(e4);
		list.add(e1);
		list.add(e3);
		list.add(e2);
		Collections.sort(list,Collections.reverseOrder());
		for(Entity e:list){
			System.out.println(e.getName()+"\t"+e.getCount());
		}
		
	}
	

}
