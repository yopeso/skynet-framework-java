#!/bin/bash

# setup git
git config user.name "leonardmustatea"
git config user.email "leonard.mustatea@gmail.com"
git checkout main

latest_release=$(mvn mvn -s .circleci/settings.xml -q \
                      -Dexec.executable="echo" -Dexec.args='${project.version}' \
                      --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

echo "Latest released version" $latest_release

git checkout ${CIRCLE_BRANCH}

package_version=$(mvn -f pom.xml -q -Dexec.executable="echo" \
                      -Dexec.args='${project.version}' --non-recursive exec:exec)
echo "Current package version" $package_version

# Comma-delimit the versions, translate the commas into newlines,
# sort by version (-V) in reverse order (-r), and return the top result
highest_semver=$(echo "$package_version,$latest_release" | tr ',' '\n' | sort -rV | head -1)

# If the highest version is the one from main branch, your package version is behind
if [ $highest_semver == $latest_release ]; then
  echo "Package version already exists"
  exit 1;
fi