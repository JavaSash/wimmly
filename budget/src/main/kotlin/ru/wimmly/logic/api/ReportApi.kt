package ru.wimmly.logic.api

import org.springframework.web.bind.annotation.*
import ru.wimmly.logic.model.report.Balance
import ru.wimmly.logic.model.report.CustomPeriodRq
import ru.wimmly.logic.model.report.PeriodReport
import ru.wimmly.logic.service.BalanceService
import ru.wimmly.logic.service.ReportService

@RestController
@RequestMapping("/api/report")
class ReportApi(
    private val balanceService: BalanceService,
    private val reportService: ReportService
) {
    @GetMapping("/balance/{userId}")
    fun getBalance(@PathVariable userId: String): Balance = balanceService.getBalance(userId)

    @GetMapping("/today/{userId}")
    fun today(@PathVariable userId: String): PeriodReport = reportService.formTodayReport(userId)

    @GetMapping("/week/{userId}")
    fun thisWeek(@PathVariable userId: String): PeriodReport = reportService.formThisWeekReport(userId)

    @GetMapping("/month/{userId}")
    fun thisMonth(@PathVariable userId: String): PeriodReport = reportService.formThisMonthReport(userId)

    @GetMapping("/year/{userId}")
    fun thisYear(@PathVariable userId: String): PeriodReport = reportService.formThisYearReport(userId)

    @PostMapping("/period")
    fun custom(@RequestBody rq: CustomPeriodRq): PeriodReport = reportService.reportForPeriod(
        userId = rq.userId,
        from = rq.from,
        to = rq.to,
        label = "Период с ${rq.from} по ${rq.to}"
    )
}