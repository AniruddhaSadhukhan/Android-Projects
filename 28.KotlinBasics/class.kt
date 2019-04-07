fun main(args: Array<String>) {
	
	//CLASSES WITH PRIMARY CONSTRUCTOR
	
	//Primary constructor
	class Student1(val name: String, var age: Int){
			
		}
	
	var s1 = Student1("Ani",21)
	s1.age +=1
	println("${s1.name} - ${s1.age}")
	//-----------------------------------------------------
	
	//Primary constructor & Initializer block
	class Student2(name: String, age: Int){
		val name: String
		var age: Int
		
		init{
			this.name = name.capitalize()
			this.age = age
			
			println("${this.name} - ${this.age}")
		}
	}
	
	var s2 = Student2("mary",21)
	//-----------------------------------------------------
	
	//Primary constructor & Initializer block version 2
	// and having default value
	class Student4(_name: String = "UNKNOWN", _age: Int = 0){
		val name = _name.capitalize()
		var age = _age
		
		init{
			println("${this.name} - ${this.age}")
		}
	}
	
	var s4_1 = Student4()
	var s4_2 = Student4("Jack")
	var s4_3 = Student4("Jill",50)
	//-----------------------------------------------------
	//-----------------------------------------------------
	
	//CLASSES WITH SECONDARY CONSTRUCTOR
	
	class Student5 {
		val name: String
		var age: Int
		
		constructor(name: String, age: Int)
		{
			this.name = name.capitalize()
			this.age = age
		}
		
		constructor()
		{
			this.name = "Unknown Student"
			this.age = 0
		}
		
		fun studentInfo()
		{	
			println("${this.name} - ${this.age}")
		}
	}
	
	var s5_1 = Student5()
	s5_1.studentInfo()
	var s5_2 = Student5("rocky",50)
	s5_2.studentInfo() 
}