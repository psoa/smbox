package br.com.psoa.smbox

object Control {
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): Unit =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}
