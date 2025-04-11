package com.github.apien.chesszio.engine
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.numeric.*
import io.github.iltotore.iron.constraint.numeric.Interval.Closed
import zio.json.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zioJson.given
import sttp.tapir.codec.iron.*
import sttp.tapir.codec.iron.given
import sttp.tapir.codec.iron.*
type Row = Row.T

object Row extends RefinedType[Int, DescribedAs[Closed[0, 7], "Row index should be in range <0,7>"]] {
  val at0: Row = Row(0)
  val at1: Row = Row(1)
  val at2: Row = Row(2)
  val at3: Row = Row(3)
  val at4: Row = Row(4)
  val at5: Row = Row(5)
  val at6: Row = Row(6)
  val at7: Row = Row(7)

  extension (row: Row) {
    def plus(that: Int): Option[Row] = {
      val result: Int = that + row
      Row.option(result)
    }

    def minus(that: Int): Option[Row] = {
      val result: Int = (row: Int) - that
      Row.option(result)
    }
  }
}

type Column = Column.T

object Column extends RefinedType[Int, DescribedAs[Closed[0, 7], "Colum index should be in range <0,7>"]] {
  val at0: Column = Column(0)
  val at1: Column = Column(1)
  val at2: Column = Column(2)
  val at3: Column = Column(3)
  val at4: Column = Column(4)
  val at5: Column = Column(5)
  val at6: Column = Column(6)
  val at7: Column = Column(7)

  extension (column: Column) {
    def plus(that: Int): Option[Column] = {
      val result: Int = that + column
      Column.option(result)
    }

    def minus(that: Int): Option[Column] = {
      val result: Int = (column: Int) - that
      Column.option(that)
    }
  }
}

case class Square(column: Column, row: Row)

object Square {
  given encoder: JsonEncoder[Square] = DeriveJsonEncoder.gen[Square]
  given decoder: JsonDecoder[Square] = DeriveJsonDecoder.gen[Square]
//  given jsonCodec: JsonCodec[Square] = DeriveJsonCodec.gen

  def attempt(column: Int, row: Int): Option[Square] = for {
    column <- Column.option(column)
    row    <- Row.option(row)
  } yield Square(column, row)
}
