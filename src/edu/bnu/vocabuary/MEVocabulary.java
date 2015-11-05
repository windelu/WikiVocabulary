package edu.bnu.vocabuary;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import FileIO.FileInput;
import FileIO.FileOutput;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;
/**
 * 
 * @author dlwen
 * 获得mention-->entitys
 *
 *从文本中抽取：extractvocabulary(path)
 *输出到文本：outputVocabulary（outpath）  mention \t\t\t entity1 \t\t mention2....
 *
 *从文本中获取 loadVocabulary(path)
 *
 *获取mentionCount大于m，enitity pro大于n：deleteByNum(m,n)
 *
 *
 */
public class MEVocabulary {
	
	public static  HashMap<String,Map<String,Entity>> MEvocabulary;
	public static  HashMap<String,Integer> mentionCount;
	
	public MEVocabulary(){
		MEvocabulary=new HashMap<String,Map<String,Entity>>();
		mentionCount=new HashMap<String,Integer>();
	}

	public Map<String,Entity> getEntities(String mention){
		Map<String,Entity> result=MEvocabulary.get(mention);
		if(result==null)return null;
		return Collections.unmodifiableMap(result);
	}
	
	public boolean containMention(String m){
		return MEvocabulary.keySet().contains(m);
	}
	
	public boolean addMentionEntity(String mention,String entity, int count){
		Map<String,Entity> entities=MEvocabulary.get(mention);
		boolean result=false;
		if(entities==null){
			entities=new HashMap<String,Entity>();
			Entity e=new Entity(entity);
			e.setCount(count);
			//e.increaseCount();
			entities.put(entity, e);
			MEvocabulary.put(mention, entities);
			result=true;
		}else{
			Entity e=entities.get(entity);
			if(e==null){
				e=new Entity(entity);
				entities.put(entity, e);
				e.setCount(count);
				result=true;
			}
		}
		
		if(result){
			Integer mentioncount=mentionCount.get(mention);
			if(mentioncount==null){
				mentioncount=0;
			}
			mentioncount=mentioncount+count;
			mentionCount.put(mention, mentioncount);
			
			//update prob for other entities of this mention
			for(String s:entities.keySet()){
				entities.get(s).updateProb(mentioncount);
			}
		}		
		return result;
		
	}
	
	public void loadVocabulary(String path){
		MEvocabulary=new HashMap<String,Map<String,Entity>>();
		mentionCount=new HashMap<String,Integer>();
		
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			String [] s=line.split("\t\t\t");
			if(s.length<2){
				System.err.println("reading error: "+line);
				continue;
			}
			String mention=s[0];
			int mc=0;
			Map<String,Entity> entities=new HashMap<String,Entity>();
			String [] ss=s[1].split("\t\t");
			for(String es:ss){
				String [] eess=es.split("\t");
				if(eess.length!=3){
					System.err.println("reading inmap error: "+line+"\t\t"+es);
					continue;
				}
				Entity entity=new Entity(eess[0]);
				entity.setCount(Integer.parseInt(eess[1]));
				entity.setProb(Double.parseDouble(eess[2]));
				entities.put(entity.getName(), entity);
				mc+=entity.getCount();
			}
			
			MEvocabulary.put(mention, entities);
			mentionCount.put(mention, mc);
		}
		
		reader.closeReader();
	}
	
	public void loadVocabulary(String path, Set<String> kept_entities){
		MEvocabulary=new HashMap<String,Map<String,Entity>>();
		mentionCount=new HashMap<String,Integer>();
		
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			String [] s=line.split("\t\t");
			if(s.length<2){
				System.err.println("reading error: "+line);
				continue;
			}
			String mention=s[0];
			int mc=0;
			Map<String,Entity> entities=new HashMap<String,Entity>();
			String [] ss=s[1].split(";");
			for(String es:ss){
				String [] eess=es.split(",");
				if(eess.length!=3){
					System.err.println("reading error: "+es);
					continue;
				}
				if(kept_entities.contains(eess[0])){
					Entity entity=new Entity(eess[0]);
					entity.setCount(Integer.parseInt(eess[1]));
					entity.setProb(Double.parseDouble(eess[2]));
					entities.put(entity.getName(), entity);
					mc+=entity.getCount();
				}
			}
			
			if(entities.size()!=0){
				MEvocabulary.put(mention, entities);
				mentionCount.put(mention, mc);
			}
			
		}
	}
	
	public void outputVocabulary(String path){
		FileOutput writer=new FileOutput(path, false);
		for(String mention:MEvocabulary.keySet()){
			StringBuffer sb=new StringBuffer();
			if(MEvocabulary.get(mention).size()!=0){
				sb.append(mention+"\t\t\t");
				Map<String,Entity> entities=MEvocabulary.get(mention);
				 for(String e:entities.keySet()){
					 Entity entity=entities.get(e);
					 sb.append(entity.toString()+"\t\t");
				 }
				 
				 writer.write(sb.toString());
			}
			
		}
		writer.closeWriter();
	}
	
	public void updateProb(){
		 for(String mention:MEvocabulary.keySet()){
			 Integer mcount=mentionCount.get(mention);
			 Map<String,Entity> entities=MEvocabulary.get(mention);
			 for(String e:entities.keySet()){
				 Entity entity=entities.get(e);
				 entity.updateProb(mcount);
			 }
		 }
	}
	
	/**
	 * change the entity lowerCase to UpperCase
	 * @param entity
	 * @return
	 */
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
	
	
	/**
	 * extract vocabulary from baidu
	 * @param dumpPath
	 * @param redirections
	 * @param articleIdMap
	 */
	
	public void extractVocabularyFromVoca(String path){
		FileInput reader=new FileInput(path);
		String line=null;
		while((line=reader.readLine())!=null){
			if(line.contains("|")){
		
				String entity=StringUtils.substringBefore(line, "|");
				entity=entity.replace("[[", "").trim();

				String mention=StringUtils.substringAfter(line, "|");
				mention=mention.replace("]]", "").trim();

				if ((UsedLink(mention.trim()))&&(mention.length()>1)) {				
					entity=firstToUper(entity);
				addMentionEntity(mention, entity);
				}
			}else {
				String entity=line.replace("[[", "");
				entity=entity.replace("]]", "");
				if((UsedLink(entity.trim()))&&(entity.length()>1)){		
					//??不知道需不需要也在entity修改首字母。
					addMentionEntity(firstToUper(entity), firstToUper(entity));
						
				}
			}
		}
		if (MEvocabulary.containsKey("")) {
			MEvocabulary.remove("");
		}
		
		reader.closeReader();
		System.out.println("Vocabulary finish");
		updateProb();
		System.out.println("Vocabulary update");
	}	

	private boolean UsedLink(String s){
		boolean judge=true;
		if(s.startsWith("#")&&s.trim().length()==2){
			judge=false;
		}
		return judge;
	}
	
	private void addMentionEntity(String mention,String entity){
		Map<String,Entity> entities=MEvocabulary.get(mention);
		if(entities==null){
			entities=new HashMap<String,Entity>();
			Entity e=new Entity(entity);
			e.increaseCount();
			entities.put(entity, e);
			MEvocabulary.put(mention, entities);
		}else{
			Entity e=entities.get(entity);
			if(e==null){
				e=new Entity(entity);
				entities.put(entity, e);
			}
			e.increaseCount();
		}
		
		Integer count=mentionCount.get(mention);
		if(count==null){
			count=0;
		}
		count=count+1;
		mentionCount.put(mention, count);
	}
	
//	protected boolean validArticleTitle(String title) {
//		title = title.toLowerCase();
//		if (title.startsWith("template:") || title.startsWith("file:")
//				|| title.startsWith("category:")
//				|| title.startsWith("wikipedia:")
//				|| title.startsWith("mediawiki:")
//				|| title.startsWith("portal:") || title.startsWith("help:")) {//|| title.endsWith("(disambiguation)")
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * transform anchored text into the same as the title of the article
	 * @param s
	 * @return
	 */
//	protected String cleanLinks(String s){
//		if(s.contains("#")){
//			s=StringUtils.substringBefore(s, "#");
//		}
//		
//		if(s.contains("_")){
//			s=StringUtils.replace(s, "_", " ");
//		}
//		
//		return s;
//	}
	
	public void outputMentionDict(String path,boolean countFitst, String tag){
		FileOutput out=new FileOutput(path,false);
		for(String mention:this.mentionCount.keySet()){
			int count=this.mentionCount.get(mention);
			if(count==0)count=this.MEvocabulary.get(mention).size();
			if(countFitst)out.write(mention+"\t"+count+"\t"+tag);
			else out.write(mention+"\t"+tag+"\t"+count);
		}
		out.closeWriter();
	}
	
	
/**
 * 
 * @param mentionnum
 * @param entityPro
 */

   public void deleteByNum(int mentionnum,double entityPro ){

//	   Map<String,Map<String, Entity>> newVocabulary=new HashMap<String, Map<String,Entity>>();
	   Map<String,Map<String, Entity>> mentionDelete=new HashMap<String, Map<String, Entity>>();
	   Map<String, Integer> mentiondeleteCout=new HashMap<String, Integer>();
	   Set<String> set=MEvocabulary.keySet();
	   for(String m:set){
		   if (mentionCount.get(m)<mentionnum){

			mentionDelete.put(m, MEvocabulary.get(m));
			mentiondeleteCout.put(m, mentionCount.get(m));
		}else {

			Map<String, Entity> entityDelete=new HashMap<String, Entity>();
			Map<String, Entity> entities=MEvocabulary.get(m);
			if(entities!=null){
				Set<String> set2=entities.keySet();
				
				for(String e:set2 ){
					Entity en=entities.get(e);	
					if((en!=null)){
						if((en.getName()!=null)&&(en.getProb()<entityPro)&&(!(en.getName().trim().equals(m)))){
							entityDelete.put(e, en);
						}
						
					
					}
						 
				}
				
				if(entityDelete!=null){
					Set<String>s2=entityDelete.keySet();
					for(String ss:s2){
						entities.remove(ss);
						int newCount=mentionCount.get(m)-1;
					
							mentionCount.put(m, newCount);					
						
						
						
					}
		
				}
			}
			
			
		}
	   }
      if (mentiondeleteCout!=null) {
    	  Set<String>s1=mentiondeleteCout.keySet();
   	   for(String s:s1){
   		 MEvocabulary.remove(s);
   		 mentionCount.remove(s);
   	   }
	}
	   
     System.out.println("delete some vocabulary");
   }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*if(args.length!=4){
			System.out.print("error parameters");
			return;
		}
		String dumpFile=args[0];
		String redFile=args[1];
		String vocaFile=args[2];
		String mentionDict=args[3];
		
		Map<String,String> redirections=LoadMap.loadLabel2LabelMap(redFile,false);		
		
		Vocabuary voca=new Vocabuary();
		voca.extractVocabularyFromSingleWikiFile(dumpFile,redirections);
		voca.updateProb();
		voca.outputMentionDict(mentionDict);
		voca.outputVocabulary(vocaFile);*/
		
		if(args.length==4){
			System.out.println("start");
			String input=args[0];
			String outVocabulary=args[1];
			String mentionNum=args[2];
			String entityPro=args[3];
			MEVocabulary demo=new MEVocabulary();
			System.out.println("start to extract vocabulary");
			demo.extractVocabularyFromVoca(input);
			System.out.println("start to delete ");
			
			demo.deleteByNum(Integer.parseInt(mentionNum.trim()), Double.parseDouble(entityPro.trim()));
			System.out.println("output Vocabulaty to text");
		    demo.outputVocabulary(outVocabulary);
		    System.out.println("finish");
		}
		else if(args.length==1) {
			String VocabularyText=args[0];
			MEVocabulary demo=new MEVocabulary();
			System.out.println("start to get vocabulary from the vocabularyText"+VocabularyText);
			demo.loadVocabulary(VocabularyText);
			System.out.println("finish !! ");
			System.out.println("the size is "+demo.MEvocabulary.size());
			
		}
		else {
			
			long startTime = System.currentTimeMillis();
			
//			demo.extractVocabularyFromVoca("F:/data/wikidata/entities1000000.txt");
			MEVocabulary demo=new MEVocabulary();
			demo.extractVocabularyFromVoca("F:/data/wikidata/entities1000000.txt");
			System.out.println(demo.mentionCount.size());
		    demo.deleteByNum(3, 0.30);
			demo.outputVocabulary("C:/Users/zcwang/Desktop/result.txt");
			System.out.println(demo.mentionCount.size());
//			System.out.println(demo.MEvocabulary.containsKey("A2"));
//			System.out.println(demo.MEvocabulary.get("A2").toString());
			System.out.println("finish");
			
			
		
			//test loadVocabulary
			demo.loadVocabulary("C:/Users/zcwang/Desktop/result.txt");
			Set<String> set=demo.MEvocabulary.keySet();
			for(String s:set){
				System.out.print("mention is : "+s+"\t\t\t");
				Map<String,Entity> entities=demo.MEvocabulary.get(s);
				Set<String> set2=entities.keySet();
				for(String e:set2){
					System.out.print(entities.get(e).toString());
				}
				System.out.println();
			}
			long endTime = System.currentTimeMillis();
			System.out.println("time"+(endTime-startTime)+"ms");
			
			
			
		}
		
	}

}
