#!/usr/bin/env bash

set -x
set -e
set -u
set -o pipefail

SCRIPT_CURRENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

CHALLENGE_ID=$1
JACOCO_TEST_REPORT_XML_FILE="${SCRIPT_CURRENT_DIR}/build/reports/jacoco/test/jacocoTestReport.xml"
mkdir -p ${SCRIPT_CURRENT_DIR}/target
JAVA_CODE_COVERAGE_INFO="${SCRIPT_CURRENT_DIR}/coverage.tdl"

export JAVA_OPTS=${JAVA_OPTS:-""}
export GRADLE_OPTS=${GRADLE_OPTS:-""}

( . ${SCRIPT_CURRENT_DIR}/gradlew -p ${SCRIPT_CURRENT_DIR} -q clean test jacocoTestReport --console=plain 1>&2 )

[ -e ${JAVA_CODE_COVERAGE_INFO} ] && rm ${JAVA_CODE_COVERAGE_INFO}

if [ -f "${JACOCO_TEST_REPORT_XML_FILE}" ]; then
    PERCENTAGE=$(( 0 ))
    echo ${PERCENTAGE} > ${JAVA_CODE_COVERAGE_INFO}
    COVERAGE_OUTPUT=$(xmllint --xpath '//package[@name="befaster/solutions/'${CHALLENGE_ID}'"]/counter[@type="INSTRUCTION"]' ${JACOCO_TEST_REPORT_XML_FILE})
    if [[ ! -z "${COVERAGE_OUTPUT}" ]]; then
        MISSED=$(echo ${COVERAGE_OUTPUT} | awk '{print missed, $3}' | tr '="' ' ' | awk '{print $2}')
        COVERED=$(echo ${COVERAGE_OUTPUT} | awk '{print missed, $4}' | tr '="' ' '| awk '{print $2}')
        TOTAL_LINES=$((MISSED + $COVERED))
        PERCENTAGE=$(($COVERED * 100 / $TOTAL_LINES))
    fi
    echo ${PERCENTAGE} > ${JAVA_CODE_COVERAGE_INFO}
    cat ${JAVA_CODE_COVERAGE_INFO}
    exit 0
else
    echo "No coverage report was found"
    exit -1
fi
