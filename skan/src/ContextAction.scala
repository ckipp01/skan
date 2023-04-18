package skan

/** The various actions you can take from the context menu.
  */
enum ContextAction:
  case EditCurrentContext, DeleteCurrentContext, CreateNewContext

  def pretty() = this match
    case EditCurrentContext   => "Edit current context"
    case DeleteCurrentContext => "Delete current context"
    case CreateNewContext     => "Create new context"
