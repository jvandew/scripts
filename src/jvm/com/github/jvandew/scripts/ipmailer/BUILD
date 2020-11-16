load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library")

scala_library(
    name = "ipmailer",
    srcs = glob(["*.scala"]),
    deps = [
        "@maven//:org_simplejavamail_simple_java_mail",
    ],
)

