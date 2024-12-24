package com.example.scala3.advanced

import scala.concurrent.ExecutionContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import scala.concurrent.Await
import scala.util.Random
import scala.concurrent.duration._
import scala.concurrent.Promise

object Futures extends App {

  val executor: ExecutorService = Executors.newFixedThreadPool(4)
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(executor)

  def calculateMeaningOfLife(): Int = {
    Thread.sleep(1000)
    42
  }

  // a future = an async computation that will finish at some point
  val aFuture: Future[Int] =
    Future.apply(calculateMeaningOfLife())

  // Option[Try[Int]], because
  // - we don't know if we have a value
  // - if we do, that can be a failed computation
  val futureInstantResult: Option[Try[Int]] =
    aFuture.value // inspects the value of the future RIGHT NOW

      // callbacks
  aFuture.onComplete {
    case Success(value) =>
      println(s"I've completed with the meaning of life: $value")
    case Failure(ex) => println(s"My async computation failed: $ex")
  }

  case class Profile(id: String, name: String) {
    def sendMessage(anotherProfile: Profile, message: String) =
      println(
        s"${this.name} sending message to ${anotherProfile.name}: $message"
      )
  }

  object SocialNetwork {

    // "database"
    val names = Map(
      "rtjvm.id.1-daniel" -> "Daniel",
      "rtjvm.id.2-jane" -> "Jane",
      "rtjvm.id.3-mark" -> "Mark"
    )

    // friends "database"
    val friends = Map(
      "rtjvm.id.2-jane" -> "rtjvm.id.3-mark"
    )

    val random = new Random()

    // "API"
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetch something from the database
      Thread.sleep(random.nextInt(300)) // Simulate the time delay
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // problem: sending a message to my best friend
  def sendMessageToBestFriend(accountId: String, message: String): Unit = {
    // 1 - call fetchProfile
    // 2 - call fetchBestFriend
    // 3 - call profile.sendMessage(bestFriend)
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    // using onComplete is a hassle here, so we use functional composition
    val bestFriendFuture: Future[Unit] =
      profileFuture.flatMap(profile =>
        SocialNetwork
          .fetchBestFriend(profile)
          .map(bestFriend => profile.sendMessage(bestFriend, message))
      )
  }

  def sendMessageToBestFriend_v2(
      accountId: String,
      message: String
  ): Future[Unit] = {
    for {
      profile <- SocialNetwork.fetchProfile(accountId)
      bestFriend <- SocialNetwork.fetchBestFriend(profile)
    } yield profile.sendMessage(
      bestFriend,
      message
    ) // identical to previous sendMessageToBestFriend
  }

  // fallbacks
  val profileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("rtjvm.id.0-dummy", "Forever alone")
  }

  // recoverWith (like flatMap for recovers) (if both futures fail, the error comes from the LAST failed future)
  val aFetchedProfileNoMatterWhat: Future[Profile] =
    SocialNetwork.fetchProfile("unknown id").recoverWith { case e =>
      SocialNetwork.fetchProfile("rtjvm.id.0-dummy")
    }

  // fallbackTo (if both futures fail, the error comes from the FIRST failed future)
  val fallbackProfile: Future[Profile] = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("rtvjm.id.0-dummy"))

  // AWAITING
  // Block for a future
  case class User(name: String)
  case class Transaction(
      sender: String,
      receiver: String,
      amount: Double /*never use Double for banking since it loses precision*/,
      status: String
  )

  object BankingApp {
    // "API"
    def fetchUser(name: String): Future[User] = Future {
      println("Fetching user...")
      // simulate some DB fetching
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(
        user: User,
        merchantName: String,
        amount: Double
    ): Future[Transaction] = Future {
      println("Creating transaction...")
      // simulate payment
      Thread.sleep(500)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    // "external API"
    def purchase(
        username: String,
        item: String,
        merchantName: String,
        price: Double
    ): String = {
      // 1. fetch user
      // 2. create transaction
      // 3. wait for the transaction to finish
      val transactionStatusFuture: Future[String] = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, price)
      } yield transaction.status

      // blocking call
      Await.result(
        transactionStatusFuture,
        2.seconds
      ) // throws TimeoutException if the future doesn't finish within 2 seconds
    }
  }

  /*
    Promises: Allows you to manually control the completion of a Future

    They're useful when passing the promise through other methods, so that inside those methods we control when the future inside the promise
    can be completed
   */

  def demoPromises() = {
    val promise = Promise[Int]()
    val futureInside: Future[Int] = promise.future

    // thread 1 - 'consumer': monitor the future for completion
    futureInside.onComplete {
      case Success(value) =>
        println(s"[consumer] I've just been completed with $value")
      case Failure(ex) => ex.printStackTrace()
    }

    // thread 2 - 'producer'
    val producerThread = new Thread(() =>
      println("[producer] Crunching numbers...")
      Thread.sleep(100)
      promise.success(42)
      println("[producer] I'm done.")
    )

    producerThread.start()
  }

  // executor.shutdown()

  // println(futureInstantResult)
  // println("Sending message")
  // sendMessageToBestFriend_v2(
  //   "rtjvm.id.2-jane",
  //   "Hey best friend, nice to talk to you again"
  // )
  // println("Purchasing...")
  // BankingApp.purchase("daniel-234", "shoes", "merchan-987", 3.56)
  // println("purchase complete")
  demoPromises()
  Thread.sleep(3000)
  executor.shutdown()
}
