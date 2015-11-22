## WikiVocabulary

## file introdution
* lib: related library
* runJar: runnable jar files,you can input related command,and get result
* src....: related code 

##  vocabulary save path 
* 172.16.216.235:/home/QueryEL/data/
* emvocabulary2.txt:entity-mentions
* mevocabulary.txt: mention-entities

## code inroduction
* EMVocabulary.java :get entity-Memtions Map  
         vocabulary：\<entityName,Map\<String,Mention(entity type)>>
* MEVocabulary.java :get entity-Memtions Map  
         vocabulary：\<mentionName,Map\<String,entity>>

## runJar command:
### emVocabulary
* extract Entity Mentions Vocabulary

* get part of the vocabulary:if you have a Threshold ,and set the first num of the mentions:
* 
 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar emVocabulary.jar loadVocabularyPath outputNewPath mentionNum 
*
   you will get a text , every line is [[ entity  |  mention  ]] . 

### meVocabulary
   
* extract  Mention  Entities Vocabulary
   **if you want to get meVocabulary from text and have a related outpue  , set mention threshold value is 10,entities  threshold value is 0.05
      
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar meVocabulary.jar inputFilename outputFilename 10 0.05
  ** the inputFilename is the outputNewPath that is the result of the emvocabualry.
  ** the outputFile : mention    \\t\\t\\t  entity1.name\\tentity1.count\\tentity1.pro  (\\t\\t)    entity2.name\\tentity2.count\\tentity2.pro
* loadVocabulary:if you have a existed Vocabularyfile, and you want to get vocabulary:
* 

 
         #java -XX:-UseGCOverheadLimit -Xmx30G -jar meVocabulary.jar inputVocabualryFilename 



