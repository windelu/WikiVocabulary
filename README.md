## WikiVocabulary

## file introdution
* lib: related library
* runJar: runnable jar files,you can input related command,and get result
* src....: related code 
## code inroduction
* EMVocabulary.java :get entity-Memtions Map  
         vocabulary：\<entityName,Map\<String,Mention(entity type)>>
* MEVocabulary.java :get entity-Memtions Map  
         vocabulary：\<mentionName,Map\<String,entity>>

## runJar command:

 * meVocabulary：if you want to get meVocabulary from text and have a related outpue  , set mention threshold value is 10,entities threshold value is 0.05
 * 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar meVocabulary.jar inputFilename outputFilename 10 0.05
   * the outputFile : mention    (\t\t\t)   entity1.name\tentity1.count\entity1.pro  (\t\t)    entity2.name\tentity2.count\entity2.pro

   * loadVocabulary:if you have a existed Vocabularyfile, and you want to get vocabulary:
 * 
 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar meVocabulary.jar inputVocabualryFilename 

 * emVocabulary：if you want to get emVocabulary from text and have a related outpue  , set mention threshold value is 
 * 
 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar emVocabulary.jar inputFilename outputFilename 

   *  loadVocabulary:if you have a existed Vocabularyfile, and you want to get vocabulary:
 * 
 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar emVocabulary.jar inputVocabualryFilename 

   *  get part of the vocabulary:if you have a Threshold ,and set the first num of the mentions:
 * 
 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar emVocabulary.jar loadVocabularyPath outputNewPath mentionNum 


