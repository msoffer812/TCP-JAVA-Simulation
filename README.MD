	This protocol will uses more complicated strings both as metadata and to signal if a package was corrupted. Essentially, the idea is that if the signalling strings are all in order and the metadata is present, it's pretty unlikely that something else was corrupted and not the long, complicated strings. 
	For example, let's say we are trying to send the words of 'The Wheels on the Bus' to the client:
 The server will turn the string into an array, each element consisting of a single word. Before sending each element, the server will concatenate a prearranged string + the index number + a pre-arranged string + string length + prearranged string + the word. 
	For example, 'wheels', which will be index 1 and length 6:
	1. Since this program involves user input, the server recognizes user input as it starts with a '~'.
	2. We have previously arranged so the client recognizes 'xbah*&bdj~' as a delineator between metadata and data, 	'vjdfjfiejro--$$@' as a delineator between string position and string length, and 'hrhgig#$@#' as the mandatory beginning of 	each packet. So, we will concatenate everything together using stringbuilder to save time complexity, in case it's a 	long string:
		hrhgig#$@# + 1 + vjdfjfiejro--$$@ + 6 + xbah*&bdj + wheels = hrhgig#$@#1vjdfjfiejro--$$@6xbah*&bdj~wheels

	3. Now, we will send hrhgig#$@#1vjdfjfiejro--$$@6xbah*&bdj~wheels to the client. When the client recieves it, it will check for a few things:
		a. There is the pattern of hrhgig#$@# + int + vjdfjfiejro--$$@ + int + xbah*&bdj~ + String. If it is missing any of the prearranged strings, or any of the ints or the folowing string is not the correct length, it will know the pattern was corrupted.
		b. The string is the length of the second int. If not, it knows the pattern was corrupted and wil not increment the 'total' variable which counts the amount of packets recieved. 
The client will have an treeMap which maps index to String. If the package isn't corrupted and the number was given, the information will be put into the treeMap.

	4. The server will send one last confirmation, which will be sent as 'end'(which indicates that it's a final confirmation) + length of array(so the amount of packages the client should have gotten).

	5.Upon receiving the last confirmation, the client will check if the size of the TreeMap equals the amount of packages it got. If not, it will loop through the keys of the map and concatenate to a string the missing indexes. It will go on until it has written into the packet all the requested indexes. This particular request is indicated with "y~%%#" and indexes are separated by '~'. 

Otherwise, we're good.

The server responds to input based on if/else statements analysing client messages. The client is structured in a do-while loop, where it will continue to request packets from the server until the treeMap's size is the length indicated in 'end'.