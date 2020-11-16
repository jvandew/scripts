load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# bazel's starlark language standard library
skylib_version = "1.0.3"
http_archive(
    name = "bazel_skylib",
    urls = [
        "https://github.com/bazelbuild/bazel-skylib/releases/download/{version}/bazel-skylib-{version}.tar.gz".format(
            version = skylib_version,
        ),
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/{version}/bazel-skylib-{version}.tar.gz".format(
            version = skylib_version,
        ),
    ],
    sha256 = "1c531376ac7e5a180e0237938a2536de0c54d93f5c278634818e0efc952dd56c",
)
load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")
bazel_skylib_workspace()

# scala setup
rules_scala_version = "376765b8cb2b82d201b05f359d3b4faa6229eefa"
http_archive(
    name = "io_bazel_rules_scala",
    strip_prefix = "rules_scala-{}".format(rules_scala_version),
    type = "zip",
    url = "https://github.com/bazelbuild/rules_scala/archive/{}.zip".format(rules_scala_version),
    sha256 = "5d3cbe75503af9a4c9ac2905c46326ce7364fe2124f0441265fcbdf12003c0d0",
)
load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")
scala_config(
    scala_version = "2.12.12",
)
load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")
scala_register_toolchains()
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_repositories")
scala_repositories()

# protobuf needed by rules_scala?
# protobuf_version="3.11.3"
# http_archive(
#     name = "com_google_protobuf",
#     url = "https://github.com/protocolbuffers/protobuf/archive/v%s.tar.gz" % protobuf_version,
#     strip_prefix = "protobuf-%s" % protobuf_version,
#     sha256 = "cf754718b0aa945b00550ed7962ddc167167bd922b842199eeb6505e6f344852",
# )

# Dependencies needed for google_protobuf.
# You may need to modify this if your project uses google_protobuf for other purposes.
# load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
# protobuf_deps()

