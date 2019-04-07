fun main(args: Array<String>) {
	
	println("Hi,")
	
	//Mutable variables
		//Implicit declaration
	var age = 22
	var name = "Ani"
		//Explicit declaration
	var x: Int = 5
	var country: String = "India"
	
	age = 25
	
	//Immutable variables
	val roll = 5
	
	println("I am $name and I am $age years old. I live in $country. My roll is $roll")
	
	
	/*~~~~~~~~~~Lists & Arrays~~~~~~~~~~~~~*/
	
	//Immutable List
		//Non-empty list
	var imlist = listOf(1,5,6,"ani",3.5)
	println(imlist.toString())
		//Empty list
	var imlist2 = listOf<Int>()
	println(imlist2.toString())
	
	//[Immutable] Array
		//Non-empty array
	var imarr = arrayOf(1,"ani",3.5)
	imarr.forEach (::println)
		//Empty array
	var imarr2 = arrayOf<Int>()
	imarr2.forEach (::println)

	//Mutable List
		//Non-empty list
	var mlist = mutableListOf(1,5,6,"ani",3.5)
	mlist.add(5)
	println(mlist.toString())
	println(mlist[5])
	println(mlist.size)
		//Empty list
	var mlist2 = mutableListOf<Int>()
	mlist2.add(5)
	println(mlist2.toString())
	
	//Single Data Type List
	var favColours: List<String> = listOf("Blue","Yellow","Red")
	println(favColours.toString())
}