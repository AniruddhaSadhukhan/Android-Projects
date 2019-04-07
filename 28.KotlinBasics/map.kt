fun main(args: Array<String>) {
	
	var dogs = mapOf("Fido" to 8, "Sara" to 17, "Sean" to 6)
	
	println(dogs["Sara"])
	
	var mdogs = mutableMapOf("Fido" to 8, "Sara" to 17, "Sean" to 6)
	
	mdogs["Tommy"] = 52
	
	mdogs["Sara"] = 52
	
	println(mdogs.toString())
}