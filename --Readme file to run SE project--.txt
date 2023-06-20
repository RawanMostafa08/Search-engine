--Readme file to run SE project--
1.run the crawler at the begining of operation to store crawled documents in MongoDB
2.run the indexer to index the crawled documents and store the data in mongoDB
3.run the Ranker to be ready to rank the results obtained from your query
4.run React project to use the UI and write your query
---------How to run our React project?--------------
*you should open a your cmd prompt and type the following command "npx create-react-app project"
*wait until it finishes, then replace the existing src with the src present in our submitted folder
*open new terminal in your directory and type the command "npm start"
*if you face any errors on your run, download any required dependencies mentioned in the error
5.you can search directly with the word or use quotation marks for Phrase Searching
6.you can also perform logic operations like and/not/or to combine results in Phrase Searching 
7.the Ranker rank the results and send them to UI through Api:http://localhost:8080/result 
8.the React recieve the data and map them to show the results
--end of Readme file--

