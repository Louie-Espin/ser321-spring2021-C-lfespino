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
	
	For this experiment, I did something similar to the Professor's video provided and divided up the work into two Branch nodes. These branches then had two Sorters each to sort the arrays
	for them. The Start task then waits for the Branches to finish joining up the complete array to give us the result.
	
	TESTS RAN:
	- Given array; TIME: ~52 milliseconds
	- Ones Array; TIME: ~14 milliseconds
	- Long Array (7 times larger than given array); TIME: ~77 milliseconds
	- Short Array (about 1/4 the size); TIME: ~4 milliseconds
	
	The results were great. Dividing up the work further is what "Divide and Conquer" algorithms are all about. Giving each branch a smaller workload means it can be sorted even faster and thus
	the results are much quicker. This was not as noticable however in the short array, as the array size was already quite small.

- 4). Explain the traffic that you see in wireshark. How much traffic is generated with this setup and do you have a way to reduce it?

	There is the initial dump of the arrays from the Branch to its Sorters, but then the branch must send in "peek/remove" commmands to each Sorter in order to get the sorted arrays.
	We should be able to reduce this large amount of traffic by having the sorters just send their array all at once, then let the Branch node sort these two nodes locally.

## Task 2: Running it outside of localhost
- 1). Do you expect changes in runtimes? Why or why not?
	
	Yes, I expect a large difference in runtimes. With the other experiments, we were running everything locally and thus did not have this faraway internet connection slowing us down.

- 2). Do you see a difference on how long it takes to sort the arrays? Explain the differences (or why there are none).
	
	Yes, because we have the Sorters running in an AWS instance, the Branch has traffic coming in and out from outside the computer. The Sorters have an easy time sorting their
	part of the array and setting up the priority queue, but the Branch must then have these Sorters send in all of their data one peek at a time, which takes significantly longer
	since it is not local.

## Task 3: How to improve
- 1). Where is the most time lost and could you make this more efficient?
	
	I believe that there is a lot of time lost when each Sorter has to send in a value and the branch sorts it, one at a time, to merge both arrays together. It would probably be more efficient
	if the Sorters sent in their entire array to the Branch at once, and have the Branch order those arrays locally.

- 2). Does it make sense to run the algorithm as a distributed algorithm? Why or why not?
	
	I think the one case where this would be beneficial is if the computers you were running the systems on were not at all powerful, and you were dealing with a very, very large set of data.
	This way the workload would be divided into smaller pieces for each computer to handle well. Otherwise, I do not think it makes sense to run mergesort as a distributed algorithm.
	As we saw in the AWS segment, having the traffic leave our computer and come back makes this significantly slower compared to running things locally. And if you wanted to run this system
	locally, then why not just use threads instead?

## Gradle Tasks Ran
Note: This is just for myself, to keep track of the gradle commands I used to run this.
- gradle Starter
- gradle Branch
- gradle Sorter --args=8000
- gradle Sorter --args=8001
- run the Sorters on AWS
- changed portH (localhost) for AWS setup to "ec2-3-129-70-113.us-east-2.compute.amazonaws.com"
