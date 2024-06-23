package machine

data class Machine(
    var water: Int = 0,
    var milk: Int = 0,
    var coffeeBeans: Int = 0,
    var disposableCups: Int = 0,
    var money: Int = 0
) {
  fun applyBuy(coffeeType: CoffeeTypes?, money: Int): Machine {
    if (coffeeType == null) {
      return this
    }
    this.water -= coffeeType.coffeeIngredients().water
    this.milk -= coffeeType.coffeeIngredients().milk
    this.coffeeBeans -= coffeeType.coffeeIngredients().coffeeBeans
    this.disposableCups -= 1
    this.money += money
    return this
  }

  fun applyFill(coffeeIngredientsAndCost: CoffeeIngredients, disposableCups: Int): Machine {
    this.water += coffeeIngredientsAndCost.water
    this.milk += coffeeIngredientsAndCost.milk
    this.coffeeBeans += coffeeIngredientsAndCost.coffeeBeans
    this.disposableCups += disposableCups
    return this
  }

  fun validateIfWeCant(coffeeType: CoffeeTypes?): Boolean {
    val waterLowLevel = this.water < (coffeeType?.coffeeIngredients()?.water ?: Integer.MAX_VALUE)
    if (waterLowLevel) {
      println("Sorry, not enough water!")
    }
    val milkLowLevel = this.milk < (coffeeType?.coffeeIngredients()?.milk ?: Integer.MAX_VALUE)
    if (milkLowLevel) {
      println("Sorry, not enough milk!")
    }
    val coffeeBeansLowLevel =
        this.coffeeBeans < (coffeeType?.coffeeIngredients()?.coffeeBeans ?: Integer.MAX_VALUE)
    if (coffeeBeansLowLevel) {
      println("Sorry, not enough coffee beans!")
    }
    val disposableCupsLowLevel = this.disposableCups < 1
    if (disposableCupsLowLevel) {
      println("Sorry, not enough cups!")
    }
    return coffeeType == null ||
        waterLowLevel ||
        milkLowLevel ||
        coffeeBeansLowLevel ||
        disposableCupsLowLevel
  }

  fun lowLevels(): Boolean {
    return this.water < 200 || this.milk < 75 || this.coffeeBeans < 12 || this.disposableCups < 1
  }
}

enum class CoffeeTypes(val numberAssociated: Int) {
  ESPRESSO(1) {
    override fun coffeeIngredients() = CoffeeIngredients(water = 250, coffeeBeans = 16)

    override fun cost(): Int = 4
  },
  LATTE(2) {
    override fun coffeeIngredients() = CoffeeIngredients(water = 350, milk = 75, coffeeBeans = 20)

    override fun cost(): Int = 7
  },
  CAPPUCCINO(3) {
    override fun coffeeIngredients() = CoffeeIngredients(water = 200, milk = 100, coffeeBeans = 12)

    override fun cost(): Int = 6
  };

  abstract fun coffeeIngredients(): CoffeeIngredients

  abstract fun cost(): Int

  companion object {
    fun fromNumber(numberAssociated: Int): CoffeeTypes? {
      return entries.find { it.numberAssociated == numberAssociated }
    }
  }
}

data class CoffeeIngredients(val water: Int = 0, val milk: Int = 0, val coffeeBeans: Int = 0)

fun main() {
  val machine = Machine(money = 550, water = 400, milk = 540, coffeeBeans = 120, disposableCups = 9)
  menu(machine)
}

fun menu(machine: Machine) {
  var optionSelected: String
  do {
    println("Write action (buy, fill, take, remaining, exit):")
    optionSelected = readln()
    when (optionSelected) {
      "remaining" -> showStatus(machine)
      "buy" -> buyCoffee(machine)
      "fill" -> fill(machine)
      "take" -> takeMoney(machine)
    }
  } while (optionSelected != "exit")
}

fun showStatus(machine: Machine) {
  println("${machine.water} ml of water")
  println("${machine.milk} ml of milk")
  println("${machine.coffeeBeans} g of coffee beans")
  println("${machine.disposableCups} disposable cups")
  println("$${machine.money} of money")
}

fun buyCoffee(machine: Machine) {
  var optionNonValid: Boolean
  var coffeeType: CoffeeTypes?
  do {
    println(
        "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
    val optionSelected = readln()
    if (optionSelected == "back") {
      return
    }
    coffeeType = CoffeeTypes.fromNumber(optionSelected.toInt())
    optionNonValid = coffeeType == null
  } while (optionNonValid)
  val insufficientIngredients = machine.validateIfWeCant(coffeeType)
  if (insufficientIngredients) {
    return
  }
  if (!machine.lowLevels()) {
    println("I have enough resources, making you a coffee!")
  }
  if (coffeeType != null) {
    machine.applyBuy(coffeeType, coffeeType.cost())
  }
}

fun fill(machine: Machine) {
  println("Write how many ml of water you want to add:")
  val water = readln().toInt()
  println("Write how many ml of milk you want to add:")
  val milk = readln().toInt()
  println("Write how many grams of coffee beans you want to add:")
  val coffeeBeans = readln().toInt()
  println("Write how many disposable cups you want to add:")
  val disposableCups = readln().toInt()
  val coffeeIngredients = CoffeeIngredients(water, milk, coffeeBeans)
  machine.applyFill(coffeeIngredients, disposableCups)
}

fun takeMoney(machine: Machine) {
  println("I gave you $${machine.money}")
  machine.money = 0
}
