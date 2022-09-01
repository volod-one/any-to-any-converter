import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

const val CHECKER_STRING = "0123456789abcdefghijklmnopqrstuvwxyz"

fun getAndValidateFirstLevelInput(): String {
    var isCorrect: Boolean = false
    var input: String = ""
    while (!isCorrect) {
        input = try {
            val line: String = readln().lowercase()
            when (line) {
                "/exit" -> {
                    isCorrect = true
                    "/exit"
                }

                else -> {
                    val (src, base) = line.split(" ").map { it.toInt() }
                    if (src in 2..36 && base in 2..36) {
                        isCorrect = true
                        line
                    } else {
                        throw Exception()
                    }
                }
            }
        } catch (e: Exception) {
            println("Invalid input.")
            println("Please enter a valid command or numbers.")
            ""
        }
    }
    return input
}

fun getAndValidateSecondLevelInput(src: Int): String {
    fun validateString(str: String) {
        val checker: String = CHECKER_STRING
        for (i in str) {
            if (checker.indexOf(i) >= src) throw Exception()
            if (i !in checker) throw Exception()
        }
    }

    var isCorrect: Boolean = false
    var input: String = ""

    while (!isCorrect) {
        input = try {
            val line: String = readln()
            when (line) {
                "/back" -> {
                    isCorrect = true
                    line
                }

                else -> {
                    val whole: String = line.substringBefore(".")
                    val fraction: String = line.replace(whole, "").replace(".", "")

                    if (fraction != "") {
                        validateString(whole)
                        validateString(fraction)
                    } else {
                        validateString(line)
                    }

                    isCorrect = true
                    line
                }
            }
        } catch (e: Exception) {
            println("This number cannot be converted.")
            println("Please enter a correct number.")
            ""
        }
    }

    return input
}

fun convert(source: Int, target: Int, value: String): String {
    fun toDecimal(base: Int, value: String): String {
        val checker: String = CHECKER_STRING
        var index: Int = value.length - 1
        var res: BigInteger = BigInteger.valueOf(0)
        for (i in value) {
            res += checker.indexOf(i).toBigInteger() * base.toBigInteger().pow(index)
            index--
        }
        return res.toString()
    }

    fun fractionToDecimal(base: Int, value: String): String {
        val checker: String = CHECKER_STRING
        var res: BigDecimal = BigDecimal("0")
        var index: Int = 1
        for (i in value) {
            res += checker.indexOf(i)
                .toBigDecimal() * (BigDecimal.ONE.setScale(10) / BigDecimal(base).pow(index))
            index++
        }
        return res.setScale(5, RoundingMode.HALF_EVEN).toString()
    }

    fun fromDecimal(target: Int, value: String): String {
        val checker: String = CHECKER_STRING
        val targetBase: BigInteger = target.toBigInteger()
        var num: BigInteger = BigInteger(value)
        var res: String = ""
        while (num >= BigInteger.valueOf(1)) {
            res += checker[(num % targetBase).toInt()]
            num /= targetBase
        }
        return res.reversed()
    }

    fun fractionFromDecimal(target: Int, value: String): String {
        val string: String = value.substringAfter(".")
        var res: BigDecimal = BigDecimal("0.$string")
        var index: Int
        var output = ""
        for (i in string) {
            index = (res * BigDecimal(target)).toInt()
            res = (res * BigDecimal(target) - BigDecimal(index))
            output += CHECKER_STRING[index]
        }
        return output
    }

    val whole: String = value.substringBefore(".")
    val fraction: String = value.replace(whole, "").replace(".", "")

    val decimalValue: String = toDecimal(source, whole)
    val targetValue: String = fromDecimal(target, decimalValue)

    return if (fraction != "") {
        val fractionDecimalValue: String = fractionToDecimal(source, fraction)
        val fractionTargetValue: String = fractionFromDecimal(target, fractionDecimalValue)
        "$targetValue.$fractionTargetValue"
    } else {
        targetValue
    }
}

fun main() {
    var isExit: Boolean = false

    // entering first level
    while (!isExit) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val firstLevelInput: String = getAndValidateFirstLevelInput()

        when (firstLevelInput) {
            "/exit" -> isExit = true
            else -> {
                var isBack: Boolean = false
                val (srcBase: Int, targetBase: Int) = firstLevelInput.split(" ").map { it.toInt() }
                // entering second level
                while (!isBack) {
                    println("Enter number in base $srcBase to convert to base $targetBase (To go back type /back)")
                    val secondLevelInput: String = getAndValidateSecondLevelInput(srcBase)

                    // todo
                    // check if input contains fractions
                    // if yes - split it and convert separately

                    when (secondLevelInput) {
                        "/back" -> isBack = true
                        else -> {
                            val result: String = convert(srcBase, targetBase, secondLevelInput)
                            println("Conversion result: $result")
                        }
                    }
                }
            }
        }
    }
}