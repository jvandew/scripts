load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary", "scala_library")

scala_library(
    name = "ipmailer",
    srcs = glob(["*.scala"]),
    visibility = [
        "//:__pkg__",
    ],
    deps = [
        "//:simple-java-mail",
    ],
)

scala_binary(
    name = "ipmailer-binary",
    main_class = "com.github.jvandew.scripts.ipmailer.IpMailer",
    deps = [
        ":ipmailer",
    ],
)
