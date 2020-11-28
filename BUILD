load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "3rdparty_repl",
    deps = [
        "@maven//:com_sun_mail_jakarta_mail",
        "@maven//:org_simplejavamail_core_module",
        "@maven//:org_simplejavamail_simple_java_mail",
    ],
)

scala_library(
    name = "repl",
    deps = [
        ":3rdparty_repl",
        "//src/jvm/com/github/jvandew/scripts/ipmailer",
    ],
)
