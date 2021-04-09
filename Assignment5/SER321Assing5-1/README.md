## Task 1: Getting Started
- 1). Explain the main structure of this code and the advantages and disadvantages of the setup of the distributed algorithm.

	STRUCTURE:
	Sorter: Sorters are nodes that have priority queue functionality. Are given parts of an array of data, then sort them with the priority queue. Sends over their data to Branch.
	Branch: Gets a big array, splits it up then sends the parts to the Sorter nodes. Branch then asks each Sorter to 'peek' and 'remove' and sorts the big array with the data given to it by the Sorters
	Starter: Starts a test of this distributed system using 'MergeSort' class. Creates a large array and sends it to the Branches. When the Branch is sorted, displays the array in the command line.
	
	PROS AND CONS:
	Each system has to do less work, this has various advantages such as
	+ Faster to complete a task for each system compared to a single system completing all the work
	+ Less load for each system to handle, this can be beneficial when the task to complete is large or the system is not too powerful
	However, a distributed algorithm also has its disadvantages now that each system must communicate with other systems
	- Makes the code more complex, can be more difficult to understand/maintain
	- Means that systems now have to spend time communicating with eachother. What if the connection is slow or disconnects?

- 2). Run the code with different arrays to sort, and include code to measure the time. In your readme, describe your experiments and your analysis of them. Whu us the result as it is? Does the distribution help? Why or why not? See this as setting up your own experiment and give me a good description and evaluation.
	
	TESTS RAN:
	- Added java.Duration and java.Instant classes to measure the time (in milliseconds) of how long it takes to complete a test. Tested using one Branch and two Sorters
	- First tested the given array; TIME: ~72 milliseconds
	- Next added an array of the same size as first array, but with all numbers equal to 1; TIME: ~22 milliseconds
	- Next, added a much longer array than the given array (7 times larger); TIME: ~122 milliseconds
	- Finally, added a much shorter array than the given array (about 1/4 the size); TIME: ~5 milliseconds
	
	EVALUATION NOTES:
	- Mergesort is already a good algorithm, with a time complexity of O(nlogn)
	- The given array gives us a time of around 72 milliseconds. I used this as a basis for when I tested the other arrays.
	- The array of ones time is interesting; The sorter nodes must have an easy time sorting this array, but I assume what slows it down is that the branch has to constantly communicate with the sorters.
	- When testing the larger array, I noticed how similar it is to run through the original array vs another array 7 times in size.
	- This is just an interesting quirk of mergesort however, as its good time complexity is more noticeable as the dataset grows.
	- I was also very surprised with the time of 5 milliseconds for the short array test.
	- I initially assumed that the communication between these systems would slow down the process, but it does not seem to be that significant at all when ran locally.
	
	Why is the result as it is?
	From my notes above, you can see that the algorithm is quite fast in general. This is due to mergesort being a fast algorithm, especially with larger datasets. As seen in my notes above,
	the communication between Branch and Sorters does not seem to impact performance too much when ran locally.
	
	Does The Distribution help?
	Yes, the distribution of the array does seem to help quite a lot, with 72 ms with a random array compared to 22 ms with the array that is already sorted but has the same size.
	Since this is merge sort, the sorter nodes must have an easy time sorting the ordered array individually, but I assume what slows it down a bit is that the branch has to constantly
	grab data from the sorters.

- 3). Experiment with the "tree" setup. What happens with more or less nodes when sorting the same array and different arrays? When does the distribution make things better? Does it even make things faster? As in the previous step expriment and describe your experiment and your results in detail.

	TESTS RAN:
	- One Sorter Node:
	
	- Two Sorter Nodes:
	
	- Three Sorter Nodes:
	
	- Four Sorter Nodes:
	

- 4). Explain the traffic that you see in wireshark. How much traffic is generated with this setup and do you have a way to reduce it?


## Task 2: Running it outside of localhost
1. Do you expect changes in runtimes? Why or why not?


2. Do you see a difference on how long it takes to sort the arrays? Explaion the differences (or why there are none).


## Task 3: How to improve
1. Where is the most time lost and could you make this more efficient?


2. Does it make sense to run the algorithm as a distributed algorithm? Why or why not?


## Gradle Tasks Ran
Note: This is just for myself, to keep track of the gradle commands I used to run this.
- gradle Starter
- gradle Sorter --args=8000
- gradle Sorter --args=8001
- run the Sorters on AWS
