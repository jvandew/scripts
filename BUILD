load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")
load("@io_bazel_rules_scala//scala:scala_import.bzl", "scala_import")

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

scala_import(
    name = "jakarta-mail",
    deps = [
        "@maven//:com_sun_mail_jakarta_mail",
    ],
    exports = [
        "@maven//:com_sun_mail_jakarta_mail",
    ],
)

scala_import(
    name = "simple-java-mail",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:org_simplejavamail_core_module",
        "@maven//:org_simplejavamail_simple_java_mail",
        ":jakarta-mail",
    ],
    exports = [
        "@maven//:org_simplejavamail_core_module",
        "@maven//:org_simplejavamail_simple_java_mail",
        ":jakarta-mail",
    ],
)
