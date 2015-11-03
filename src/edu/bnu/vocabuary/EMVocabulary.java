package edu.bnu.vocabuary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import FileIO.FileInput;
import FileIO.FileOutput;


/**
 * 
 * @author dlwen
 * 
 *
 */

public class EMVocabulary {
	public  Map<String,Map<String,Entity>> EMvocabulary=new HashMap<String, Map<String,Entity>>();
	
	public EMVocabulary(){
		EMvocabulary=new HashMap<String,Map<String,Entity>>();
	}
	
	
	public Map<String,Entity> getMentions(String entity){
		Map<String,Entity> result=EMvocabulary.get(entity);
		if(result==null)return null;
		return Collections.unmodifiableMap(result);
	}
	
	
	public boolean containEntity(String entity){
		return EMvocabulary.keySet().contains(entity);
	}
	
	
	private void addEntityMention(String entity,String mention){
		Map<String,Entity> mentions=EMvocabulary.get(entity);
		if(mentions==null){
			mentions=new HashMap<String,Entity>();
			Entity mEntity=new Entity(mention);
			mEntity.increaseCount();
			mentions.put(mention, mEntity);
			EMvocabulary.put(entity, mentions);
		}else{
			Entity mEntity=mentions.get(mention);
			if(mEntity==null){
				mEntity=new Entity(mention);
				mentions.put(mention, mEntity);
			}
			mEntity.increaseCount();
		}
	}
	
	
//	public void updateProb(){
//		 for(String mention:vocabulary.keySet()){
//			 Integer mcount=mentionCount.get(mention);
//			 Map<String,Entity> entities=vocabulary.get(mention);
//			 for(String e:entities.keySet()){
//				 Entity entity=entities.get(e);
//				 entity.updateProb(mcount);
//			 }
//		 }
//	}
	
	public void extractVocabularyFromVoca(String path){
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			if(line.contains("|")){
		      
				String entity=StringUtils.substringBefore(line, "|");
				entity=entity.replace("[[", "").trim();
               
                	String mention=StringUtils.substringAfter(line, "|");
    				mention=mention.replace("]]", "").trim();				
    				addEntityMention(entity, mention);
            
					
			}else {
				String entity=line.replace("[[", "");
				entity=entity.replace("]]", "");
				if(entity.trim().length()>1){
					addEntityMention(entity, entity);		
				}
			}
		}
		if (EMvocabulary.containsKey("")) {
			EMvocabulary.remove("");
		}
		
		reader.closeReader();
		addSameMention();
		System.out.println("Vocabulary finish");
//		updateProb();
//		System.out.println("Vocabulary update");
	}	
	
	
	private void addSameMention(){
		for(String entity:EMvocabulary.keySet()){
			Map<String,Entity> mentions=getMentions(entity);
			if (!mentions.containsKey(entity)) {
				addEntityMention(entity, entity);		
			}
	}
		
}
	
	public void outputVocabulary(String path){
		FileOutput writer=new FileOutput(path, false);
		for(String en:EMvocabulary.keySet()){
			StringBuffer sb=new StringBuffer();
			sb.append(en+"\t\t\t");
			Map<String,Entity> ms=EMvocabulary.get(en);
			 for(String m:ms.keySet()){
				 Entity mentity=ms.get(m);
				 sb.append(mentity.toString()+"\t\t");
			 }
			 
			 writer.write(sb.toString());
		}
		writer.closeWriter();
	}
	
//	

	public void loadVocabulary(String path){
		EMvocabulary=new HashMap<String,Map<String,Entity>>();
//		mentionCount=new HashMap<String,Integer>();
		
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			String [] s=line.split("\t\t\t");
			if(s.length<2){
				System.err.println("reading error: "+line);
				continue;
			}
			String mention=s[0];
//			int mc=0;
			Map<String,Entity> entities=new HashMap<String,Entity>();
			String [] ss=s[1].split("\t\t");
			for(String es:ss){
//				System.out.println(es);
				String [] eess=es.split("\t");
//				System.out.println(eess[0]+"  "+eess[1]+"  "+eess[2]);
				if(eess.length!=3){
				
					System.err.println("reading inmap error: "+mention +"   "+es);
					continue;
				}
				Entity entity=new Entity(eess[0]);
				
				entity.setCount(Integer.parseInt(eess[1]));
				entity.setCount(Integer.parseInt(eess[1].trim()));
				entity.setProb(Double.parseDouble(eess[2].trim()));
				entities.put(entity.getName(), entity);
//				mc+=entity.getCount();
			}
			
			EMvocabulary.put(mention, entities);
//			mentionCount.put(mention, mc);
		}
		
		reader.closeReader();
	}
	

	private  Map<String,ArrayList<Entity>> getorderEMList(){
		Map<String,ArrayList<Entity>> orderEMs=new HashMap<String,ArrayList<Entity>>();
		
		for(String entity:EMvocabulary.keySet()){
			
			Map<String, Entity> mentions=EMvocabulary.get(entity);
//			Map<String, ArrayList<Entity>> ordermMap=new HashMap<String, ArrayList<Entity>>();
			ArrayList<Entity> Mlist=new ArrayList<Entity>();
			for(String mn:mentions.keySet()){
				
				Mlist.add(mentions.get(mn));
			}
			Collections.sort(Mlist,Collections.reverseOrder());
			orderEMs.put(entity,Mlist);
			
		}	
		return orderEMs;
		
	}
	
	
	public void outputVocMList(String outpath,int mentionnum){
		Map<String,ArrayList<Entity>> orderlist=getorderEMList();
		FileOutput writer=new FileOutput(outpath, false);
		String newline=null;
		for(String entityname:orderlist.keySet() ){
			ArrayList<Entity> mentions=orderlist.get(entityname);
			String mentionString=null;
			for(int i=0;i<mentionnum;i++){
				mentionString=mentionString+mentions.get(i).toString()+"\t\t";
			}	
			newline=entityname+"\t\t\t"+mentionString;
			if(newline!=null){
				writer.write(newline);
			}
		}
		writer.closeWriter();
		
	}
	
	
	public static void main(String[] args) {
		System.out.println("two argument (input output):inuput ,textpath;output ,emVocabulary outpath ");
		System.out.println("one argument(input):vocabulaty path,you will get a vocabulary that save in System");
		if(args.length==2){
			String input=args[0];
			String output=args[1];
			EMVocabulary demo=new EMVocabulary();
			System.out.println("start");
			demo.extractVocabularyFromVoca(input);
		
			demo.outputVocabulary(output);
			System.out.println("finish");
			
		}
		
		else if(args.length==1){
			String vocabularypath=args[0];
			EMVocabulary demo=new EMVocabulary();
			System.out.println("start to get vocabulary from the vocabularyText"+vocabularypath);
			demo.loadVocabulary(vocabularypath);
			System.out.println("finish !! ");
			System.out.println("the size is "+demo.EMvocabulary.size());
		}
		
		else {
			EMVocabulary demo=new EMVocabulary();
//			demo.extractVocabularyFromVoca("F:/data/wikidata/entity100000.txt");
//		
//	    	demo.outputVocabulary("C:/Users/zcwang/Desktop/emresult.txt");
	    	
	    	demo.loadVocabulary("C:/Users/zcwang/Desktop/emresult.txt");
	    	System.out.println(demo.EMvocabulary.size());
	    	
	    	
			Set<String> set=demo.EMvocabulary.keySet();
			for(String s:set){
			    System.out.println();
			    Map<String, Entity> mentions=demo.EMvocabulary.get(s);
			    for(String s2:mentions.keySet()){
			    	System.out.println(s+"\t\t"+mentions.get(s2).toString());
					
				}
			}
		}
		
		
		
	}
	
	
		
}

