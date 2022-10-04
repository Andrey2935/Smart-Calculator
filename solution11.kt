import java.math.BigInteger

fun main() {
    val map = mutableMapOf<String, BigInteger>()
    invalid@ while (true) {
        var inputString = readln()

        if (inputString.isEmpty()) continue
        else if (inputString == "/help") {
            println("The program calculates the sum of numbers")
            continue
        }
        else if (inputString == "/exit") {
            println("Bye!")
            return
        } else if (inputString.matches("/.+".toRegex())) {
            println("Unknown command")
        }

        if (inputString.contains('=')) {
            inputString = inputString.replaceIndent("").replace(" +".toRegex(), "")
            val input = inputString.split(" *= *".toRegex(), 2).toMutableList()
            if (!input[0].matches("[a-zA-Z]+".toRegex())) {
                println("Invalid identifier")
                continue@invalid
            }
            try {
                map[input[0]] = input[1].toBigInteger()
            } catch (e: java.lang.NumberFormatException) {
                input[1] = input[1].removeSuffix(" ")
                if (map.containsKey(input[1])) {
                    map[input[0]] = map.getValue(input[1])
                } else println("Invalid assignment")
                continue@invalid
            }
        } else {
            var b = "($inputString)"
            b = plusSpace(b)
            for (i in map) {
                b = b.replace(i.key, i.value.toString())
            }
            if (b.contains("[a-zA-Z]".toRegex())) {
                println("Unknown variable")
                continue@invalid
            }
            val list = mutableListOf<String>()
            try {
                while (b.contains("[/*\\-+]".toRegex())) {
                    val brackets = mutableListOf<Int>()
                    var j = 0
                    for (i in b.indices) {
                        if (b[i] == '(') {
                            brackets.add(i)
                            j++
                        }
                        if (b[i] == ')') {
                            val c = try {
                                b.substring(brackets.last(), i + 1)
                            } catch (e: NoSuchElementException) {
                                println("Invalid expression")
                                continue@invalid
                            }
                            list.add(c)
                            brackets.removeAt(brackets.size - 1)
                            j--
                        }
                    }
                    if (j != 0) {
                        println("Invalid expression")
                        continue@invalid
                    }
                    val input = list[0].drop(1).dropLast(1).split(" +".toRegex()).toMutableList()
                    if (input.contains("*")) {
                        while (input.contains("*")) {
                            val x = input.indexOf("*")
                            val multi = input[x - 1].toBigInteger() * input[x + 1].toBigInteger()
                            input.removeAt(x + 1)
                            input.removeAt(x)
                            input[x - 1] = multi.toString()
                        }
                    }
                    if (input.contains("/")) {
                        while (input.contains("/")) {
                            val x = input.indexOf("/")
                            val multi = input[x - 1].toBigInteger() / input[x + 1].toBigInteger()
                            input.removeAt(x + 1)
                            input.removeAt(x)
                            input[x - 1] = multi.toString()
                        }
                    }
                    val reversInput = input.reversed()
                    val editInput = mutableListOf<BigInteger>()
                    for (i in reversInput.indices) {
                        if (!reversInput[i].matches("-?\\+?\\d+|\\++|-+|-?\\+?[a-zA-Z]+".toRegex())) {
                            println("Invalid expression")
                            continue@invalid
                        }
                        if (reversInput[i].contains("\\d".toRegex()))
                            editInput.add(reversInput[i].toBigInteger())
                        else {
                            editInput.add(BigInteger.ZERO)
                            if (reversInput[i].count { it == '-' } % 2 != 0) {
                                editInput[i - 1] = -editInput[i - 1]
                            }
                        }
                    }
                    b = b.replace(list[0], editInput.sumOf { it }.toString())
                    list.clear()
                }
            } catch (e: java.lang.IndexOutOfBoundsException) {
            }
            if (b.contains("(")) b = b.drop(1).dropLast(1)
            println(b)
        }
    }
}

fun plusSpace(a: String): String {
    return a.replace(" ", "").replace("+", " + ").replace("-", " - ")
        .replace("*", " * ").replace("/", " / ").replace("  ", "")
}