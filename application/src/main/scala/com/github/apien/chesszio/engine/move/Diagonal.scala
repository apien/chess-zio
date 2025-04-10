package com.github.apien.chesszio.engine.move

import com.github.apien.chesszio.engine.{Column, Row, Square}

import scala.annotation.tailrec

object Diagonal {

  def topLeft(source: Square): List[Square] =
    loop(
      source,
      Nil,
      _ - 1,
      _ - 1
    )

  def downRight(source: Square): List[Square] =
    loop(
      source,
      Nil,
      _ + 1,
      _ + 1
    )

  def topRight(source: Square): List[Square] =
    loop(
      source,
      Nil,
      _ + 1,
      _ - 1
    )

  def downLeft(source: Square): List[Square] =
    loop(
      source,
      Nil,
      _ - 1,
      _ + 1
    )

  @tailrec
  private def loop(
    source: Square,
    acc: List[Square],
    colShift: Column => Int,
    rowShift: Row => Int
  ): List[Square] = {

    Square.attempt(colShift(source.column), rowShift(source.row)) match {
      case None             => acc
      case Some(coordinate) => loop(coordinate, acc :+ coordinate, colShift, rowShift)
    }
  }
}
