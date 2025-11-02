<#list projects as project>
    <h1>${project.name}</h1>
    <p>Tasks:</p>
    <ol>
        <#list project.tasks as task>
            <li>${task.name}: ${task.description}</li>
        </#list>
    </ol>
</#list>