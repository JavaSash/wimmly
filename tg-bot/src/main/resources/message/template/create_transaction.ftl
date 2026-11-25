<#ftl output_format="HTML">
✅ <b>Транзакция успешно добавлена!</b>

Номер: #${data.displayId?c}
Тип: ${data.type}
Категория: ${data.category}
Сумма: ${data.amount}
Дата: ${data.formattedDate}
<#if data.comment?has_content>
Комментарий: ${data.comment}
</#if>
