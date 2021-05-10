github.dismiss_out_of_range_messages({
  error: false,
  warning: true,
  message: true,
  markdown: true
})

Dir.glob("apng-drawable/build/reports/ktlint/*/ktlint*SourceSetCheck.xml").each { |file|
  checkstyle_format.base_path = Dir.pwd
  checkstyle_format.report file.to_s
}

Dir.glob("apng-drawable/build/test-results/testDebugUnitTest/*.xml").each { |file|
  junit.parse file
  junit.report
}

android_lint.skip_gradle_task = true
android_lint.report_file = "apng-drawable/build/reports/lint-results-debug.xml"
android_lint.filtering = false
android_lint.lint(inline_mode: true)

return unless status_report[:errors].empty?

return markdown "Build Failed" if ENV["JOB_STATUS"] != "success"
