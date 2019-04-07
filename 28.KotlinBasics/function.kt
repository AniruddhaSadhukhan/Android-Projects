fun main(args: Array<String>) {
	
	fun hello(){
		println("Function has been called")
	}
	
	hello()
	hello()
	//-------------------------------------------
	fun foo(): String{
		return "Hello World"
	}
	println(foo())
	
	//-------------------------------------------
	fun add(a: Double = 5.5,b: Double = 4.5): Double{
		return a+b
	}
	println(add(5.2,4.2))
	println(add())
	
	//-------------------------------------------
	fun addNum(a: Int = 5,b: Int = 5) = a+b
	
	println(addNum(5,2))
	println(addNum())
	
	
}