package core

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import org.scalatest.Matchers

class ServiceDescriptionMapSpec extends FunSpec with Matchers {

  private val baseJson = """
    {
      "base_url": "http://localhost:9000",
      "name": "Api Doc",
      "models": {
        "user": {
          "fields": [
            %s
          ]
        }
      }
    }
  """

  it("accepts type: map, defaulting to element type of string for backwards compatibility") {
    val json = baseJson.format("""{ "name": "tags", "type": "map" }""")
    val validator = ServiceDescriptionValidator(json)
    validator.errors.mkString("") should be("")
    val tags = validator.serviceDescription.get.models.head.fields.head
    tags.`type` should be(TypeInstance(TypeContainer.Map, Type.Primitive(Primitives.String)))
  }

  it("accept defaults for maps") {
    val json = baseJson.format("""{ "name": "tags", "type": "map", "default": "{ }" }""")
    val validator = ServiceDescriptionValidator(json)
    validator.errors.mkString("") should be("")
    val tags = validator.serviceDescription.get.models.head.fields.head
    tags.default shouldBe Some("{ }")
  }

  it("validates invalid defaults") {
    val json = baseJson.format("""{ "name": "tags", "type": "map", "default": "bar" }""")
    val validator = ServiceDescriptionValidator(json)
    validator.errors.mkString("") should be("Model[user] field[tags] Default[bar] is not valid for datatype[map]")
  }

}
