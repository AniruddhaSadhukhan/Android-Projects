fun main(args: Array<String>) {
	
	// Int? -> making integer nullable
	// x!! -> making x not nullable
	var num: Int? = 28
	
	num = null
	
	println("$num")
	
	num = 5
	
	if(num!=null)
	{
		// notNullNum is now non-Nullable Integer
		var notNullNum = num!!
	}
		
	
}