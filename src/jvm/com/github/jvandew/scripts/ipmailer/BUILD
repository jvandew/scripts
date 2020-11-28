load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary", "scala_library")

scala_library(
    name = "ipmailer",
    srcs = glob(["*.scala"]),
    deps = [
        "@maven//:com_sun_mail_jakarta_mail",
        "@maven//:org_simplejavamail_core_module",
        "@maven//:org_simplejavamail_simple_java_mail",
    ],
)

scala_binary(
    name = "ipmailer-binary",
    main_class = "com.github.jvandew.scripts.ipmailer.IpMailer",
    deps = [
        ":ipmailer",
    ],
)
