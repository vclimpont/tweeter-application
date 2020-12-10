# Tweeter Application - 
## Java application to analyse tweeter users relationship

This application is a school work realised in pairs with JAVA. 

It allows a user to import .CSV files of data (often large ones) from Tweeter, and get a graphical visualization of user relationships within this file.

For each users graph, the vertices represent the concerned users with different colors and sizes depending on their influence, and an edge is created between user A and user B if A retweeted B.

For performance and readability, users are placed in different communities based on their relationships. This feature is based on the Louvain method.

Thus, when a Tweeter data file is loaded in the application, a graph of the different
communities is displayed. Communities are represented by diamonds of different sizes and colors according to their importance in the network, and an edge is created when two communities are linked.

It also displays statistical data about the graph, and the user can click on a community to get more information and even enter it. In this case, a graph representing the users of this community and their relationships is displayed. He can then click on a user to get information about the latter.

### Focus on a community
[!Community focus](https://github.com/vclimpont/tweeter-application/blob/master/Images/commufocus.png)

### Focus on users
[!Users graph](https://github.com/vclimpont/tweeter-application/blob/master/Images/usersgraph.png)
[!User focus](https://github.com/vclimpont/tweeter-application/blob/master/Images/userfocus.png)


### Sources
Louvain Method : 
https://en.wikipedia.org/wiki/Louvain_method
