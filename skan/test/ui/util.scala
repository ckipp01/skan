package skan.ui

import munit.Assertions.*
import tui.*
import tui.internal.saturating.*

import skan.ContextState
import skan.Config
import skan.NewItemState
import skan.MyListWidget

import scala.collection.mutable

/** Some utils to test the UI. These are meant for integration like testing.
  * Much of this is copied from https://github.com/oyvindberg/tui-scala and just
  * slightly adjusted for my use case.
  */
object Util:
  def checkUi(state: ContextState, expected: Buffer, config: Config) =
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      Board.render(frame, state, config)

    assertBuffer(backend, expected)

  def checkInputUi(state: NewItemState, expected: Buffer) =
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      NewItem.render(frame, state)

    assertBuffer(backend, expected)

  def checkContextMenuUi(
      contextState: ContextState,
      menuState: MyListWidget.State,
      expected: Buffer
  ) =
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      ContextMenu.render(frame, contextState, menuState)

    assertBuffer(backend, expected)

  def checkEditContextUi(
      contextState: ContextState,
      name: String,
      expected: Buffer
  ) =
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      EditContext.render(frame, contextState, name)

    assertBuffer(backend, expected)

  /** This is just the diff method from:
    *
    * https://github.com/oyvindberg/tui-scala/blob/321f6f9009a0eb3de2a6cafdab91a53e8bc1dae6/tui/src/scala/tui/Buffer.scala#L199-L228
    *
    * However, there is one key differece. Having anything other than an
    * extremely small widget with color introduces some issues where when you
    * diff things like color or modifiers get _really_ hard to test whereas if
    * you are just compaiing the symbol like I'm doing here, you can just have a
    * basic outline of your UI and ensure that at least that is correct. Until I
    * figure out a better way to test modifiers, this will suffice.
    */
  def diffBuffers(actual: Buffer, expected: Buffer): Array[(Int, Int, Cell)] =
    val previous_buffer = actual.content
    val next_buffer = expected.content
    val width = actual.area.width

    val updates = Array.newBuilder[(Int, Int, Cell)]
    // Cells invalidated by drawing/replacing preceeding multi-width characters:
    var invalidated = 0
    // Cells from the current buffer to skip due to preceeding multi-width characters taking their
    // place (the skipped cells should be blank anyway):
    var to_skip = 0
    var i = 0
    val max = math.min(actual.area.area, expected.area.area)
    while i < max do
      val current = next_buffer(i)
      val previous = previous_buffer(i)
      if (current.symbol != previous.symbol || invalidated > 0) && to_skip == 0
      then
        val x = i % width
        val y = i / width
        updates += ((x, y, next_buffer(i)))

      to_skip = current.symbol.width - 1
      val affected_width = math.max(current.symbol.width, previous.symbol.width)
      invalidated = math.max(affected_width, invalidated) - 1
      i += 1
    updates.result()
  end diffBuffers

  /** Another thing stole from tui-scala.
    *
    *   - https://github.com/oyvindberg/tui-scala/blob/321f6f9009a0eb3de2a6cafdab91a53e8bc1dae6/tests/src/scala/tui/TuiTest.scala#L11-L38
    *
    * This just provides a nicer way to diff the ui
    */
  def assertBuffer(actual: TestBackend, expected: Buffer): Unit =
    assertEquals(actual.buffer.area, expected.area)
    val diff = diffBuffers(expected, actual.buffer)
    if diff.isEmpty then return

    val debug_info = new StringBuilder("Buffers are not equal")
    debug_info.append('\n')
    debug_info.append("Expected:")
    debug_info.append('\n')
    val expected_view = bufferView(expected)
    debug_info.append(expected_view)
    debug_info.append('\n')
    debug_info.append("Got:")
    debug_info.append('\n')
    val view = bufferView(actual.buffer)
    debug_info.append(view)
    debug_info.append('\n')

    debug_info.append("Diff:")
    debug_info.append('\n')
    val nice_diff = diff.zipWithIndex
      .map { case ((x, y, cell), i) =>
        s"$i: at ($x, $y) expected ${expected.get(x, y)} got $cell"
      }
      .mkString("\n")
    debug_info.append(nice_diff)
    sys.error(debug_info.toString())
  end assertBuffer
end Util

/** Taken from
  * https://github.com/oyvindberg/tui-scala/blob/master/tests/src/scala/tui/bufferView.scala
  */
object bufferView:
  def apply(buffer: Buffer): String =
    val view = new StringBuilder(buffer.content.length + buffer.area.height * 3)
    val value: Iterator[mutable.ArraySeq[Cell]] =
      buffer.content.grouped(buffer.area.width)
    value.foreach { cells =>
      val overwritten = mutable.ArrayBuffer.empty[(Int, String)]
      var skip: Int = 0
      view.append('"')
      cells.zipWithIndex.foreach { case (c, x) =>
        if skip == 0 then view.append(c.symbol.str)
        else overwritten += ((x, c.symbol.str))
        skip = math.max(skip, c.symbol.width).saturating_sub_unsigned(1);
      }
      view.append('"')
      if overwritten.nonEmpty then
        view.append(s" Hidden by multi-width symbols: $overwritten")
      view.append('\n');
    }
    view.toString()
