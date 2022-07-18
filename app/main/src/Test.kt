import kotlin.String
import kotlin.Unit

public class Test(
  public val name: String,
) {
  public fun greet(): Unit {
    println("""hello, $name""")
  }
}

public fun main(vararg args: String): Unit {
  Greeter(args[0]).greet()
}
