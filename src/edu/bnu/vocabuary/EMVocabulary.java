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
	
	
	private String  firstToUper(String entity){
		
		String reString=null;
		if(entity.length()>0){
			if(((entity.charAt(0)>='A')&&(entity.charAt(0))<='z')&&(!Character.isLowerCase(entity.charAt(0)))){
				reString= entity;
			}else {
				
				reString= (new StringBuilder()).append(Character.toUpperCase(entity.charAt(0))).append(entity.substring(1)).toString();
			}
		}
		
//    System.out.println(reString);
		return reString;
	}

	
	public void extractVocabularyFromVoca(String path){
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			if(line.contains("|")){
		      
				String entity=StringUtils.substringBefore(line, "|");
				entity=entity.replace("[[", "").trim();
               
                	String mention=StringUtils.substringAfter(line, "|");
    				mention=mention.replace("]]", "").trim();
    				
//    				System.out.println(entity);
    				if ((UsedLink(entity.trim()))&&(entity.length()>1)) {
    					entity=firstToUper(entity);
    					addEntityMention(entity, mention);
					}
			}else {
				String entity=line.replace("[[", "");
				entity=entity.replace("]]", "");
				if ((UsedLink(entity.trim()))&&(entity.trim().length()>1)) {
					entity=firstToUper(entity.trim());
					addEntityMention(entity, entity);		
				}
			}
		}
//		if (EMvocabulary.containsKey("")) {
//			EMvocabulary.remove("");
//		}
		
		reader.closeReader();
		addSameMention();
		System.out.println("Vocabulary finish");
//		updateProb();
//		System.out.println("Vocabulary update");
	}	
	
	private boolean UsedLink(String s){
		boolean judge=true;
		if((s.startsWith("#"))||(s.startsWith(":"))){
			judge=false;
		}
		return judge;
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
			if(EMvocabulary.get(en).size()!=0){
				StringBuffer sb=new StringBuffer();
				sb.append(en+"\t\t\t");
				Map<String,Entity> ms=EMvocabulary.get(en);
				 for(String m:ms.keySet()){
					 Entity mentity=ms.get(m);
					 sb.append(mentity.toString()+"\t\t");
				 }
				 
				 writer.write(sb.toString());
			}
			
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
			if(mentions.size()!=0){
				int size=0;
				if (mentions.size()<=mentionnum){
					size=mentions.size();
				}
				else {
					size=mentionnum;
				}
				String mentionString="";
				for(int i=0;i<size;i++){
					
					mentionString=mentionString+mentions.get(i).toString()+"\t\t";
				}	
				newline=entityname+"\t\t\t"+mentionString;
				if(newline!=null){
					writer.write(newline);
				}
			}
			
		}
		writer.closeWriter();
		
	}
	
	
	public static void main(String[] args) {
		System.out.println("three argument(input output num)");
		System.out.println("two argument (input output):textpath  ,  emVocabulary outpath ");
		System.out.println("one argument(input):loadVocabularyPath ");
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
			System.out.println("start to load vocabulary from the vocabularyText"+vocabularypath);
			demo.loadVocabulary(vocabularypath);
			
			System.out.println("the size is "+demo.EMvocabulary.size());
			System.out.println("finish !! ");
		}
		else if (args.length==3) {
			String vocText=args[0];
			String saveNewVoc=args[1];
			String mentionnum=args[2];
			EMVocabulary demo=new EMVocabulary();
			System.out.println("RawVocabulary path is : "+vocText);
			System.out.println("new Vocabulary path is : "+saveNewVoc);
			System.out.println("your mention num is : "+mentionnum);
			demo.loadVocabulary(vocText);
	    	System.out.println(demo.EMvocabulary.size());
	        demo.outputVocMList(saveNewVoc, Integer.parseInt(mentionnum)); 
			System.out.println("finish");
			
		}
		
		else {
			EMVocabulary demo=new EMVocabulary();
			
			//test two arguments
			demo.extractVocabularyFromVoca("F:/data/wikidata/entities1000000.txt");

	    	demo.outputVocabulary("C:/Users/zcwang/Desktop/emresult.txt");
    	  System.out.println(demo.EMvocabulary.containsKey("D"));
	    	//test three arguments
	    	demo.loadVocabulary("C:/Users/zcwang/Desktop/emresult.txt");
	    	System.out.println(demo.EMvocabulary.size());
	        demo.outputVocMList("C:/Users/zcwang/Desktop/emlistresult.txt", 2);   	
	    	

         
				
			}
	        
	        
		}
		
		
		
	}
	
	
		


