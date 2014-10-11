pool:
   idleConnectionTestPeriod : 60
   idleMaxAge : 240
   maxConnectionsPerPartition : 9
   minConnectionsPerPartition : 3
   partitionCount : 3
   acquireIncrement : 2
   statementsCacheSize : 100
   releaseHelperThreads : 3
   queryExecuteTimeLimitInMs : 3000

common:
   type: mysql
   user: root
   pwd: 123456

ems:
{% for m in _modules_ %}
{% for tb in _modules_[m]['tables'] %}
   {{tb}}: {{m}}
{% endfor %}
{% endfor %}

ms:
{% for m in _modules_ %}
   - name: {{m}}
     master: 127.0.0.1:3306/{{_modules_[m]['db']}}
     slave: 127.0.0.1:3306/{{_modules_[m]['db']}}
{% endfor %}