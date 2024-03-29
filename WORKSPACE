load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# bazel's starlark language standard library
SKYLIB_VERSION = "1.0.3"
http_archive(
    name = "bazel_skylib",
    urls = [
        "https://github.com/bazelbuild/bazel-skylib/releases/download/{version}/bazel-skylib-{version}.tar.gz".format(
            version = SKYLIB_VERSION,
        ),
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/{version}/bazel-skylib-{version}.tar.gz".format(
            version = SKYLIB_VERSION,
        ),
    ],
    sha256 = "1c531376ac7e5a180e0237938a2536de0c54d93f5c278634818e0efc952dd56c",
)
load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")
bazel_skylib_workspace()

# external jvm dep support
RULES_JVM_VERSION = "3.3"
http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-{version}".format(version = RULES_JVM_VERSION),
    sha256 = "d85951a92c0908c80bd8551002d66cb23c3434409c814179c0ff026b53544dab",
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/{version}.zip".format(
        version = RULES_JVM_VERSION,
    ),
)
load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

# thirdparty deps here
maven_install(
    artifacts = [
        maven.artifact(
            group = "com.sun.mail",
            artifact = "jakarta-mail",
            version = "1.6.5",
        ),
        maven.artifact(
            group = "org.simplejavamail",
            artifact = "core-module",
            version = "6.4.4",
        ),
        maven.artifact(
            group = "org.simplejavamail",
            artifact = "simple-java-mail",
            version = "6.4.4",
        )
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
    maven_install_json = "//:maven_install.json",
)
load("@maven//:defs.bzl", "pinned_maven_install")
pinned_maven_install()

# scala setup
rules_scala_version = "a2b971d85cdf846ed9007eeaf022065797fdb5ec"
http_archive(
    name = "io_bazel_rules_scala",
    strip_prefix = "rules_scala-{}".format(rules_scala_version),
    type = "zip",
    url = "https://github.com/bazelbuild/rules_scala/archive/{}.zip".format(rules_scala_version),
    sha256 = "f4199485d1e013216fb69c910259a333ab20208e885ee10e5d2f06c4a37b8bc1"
)
load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")
scala_config(
    scala_version = "2.12.12",
)
# using default scala toolchain, see https://github.com/bazelbuild/rules_scala/blob/master/docs/scala_toolchain.md
# for defining a custom toolchain
load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")
scala_register_toolchains()
load("@io_bazel_rules_scala//scala:scala.bzl", "scala_repositories")
scala_repositories()

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")
rules_proto_dependencies()
rules_proto_toolchains()

# Dependencies needed for google_protobuf.
# You may need to modify this if your project uses google_protobuf for other purposes.
# load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
# protobuf_deps()

