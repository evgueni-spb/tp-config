{{/*
Expand the name of the chart.
*/}}
{{- define "config-poc.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "config-poc.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "config-poc.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "config-poc.labels" -}}
helm.sh/chart: {{ include "config-poc.chart" . }}
{{ include "config-poc.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "config-poc.selectorLabels" -}}
app.kubernetes.io/name: {{ include "config-poc.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}


{{/*
Common labels for Control Panel
*/}}
{{- define "controlPanel.labels" -}}
helm.sh/chart: {{ include "config-poc.chart" . }}
{{ include "controlPanel.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels for Control Panel
*/}}
{{- define "controlPanel.selectorLabels" -}}
app.kubernetes.io/name: {{ include "config-poc.name" . }}-control-panel
app.kubernetes.io/instance: {{ .Release.Name }}-control-panel
{{- end }}




{{/*
Create the name of the service account to use
*/}}
{{- define "config-poc.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "config-poc.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}


{{/*
Create the name of the service account to use
*/}}
{{- define "controlPanel.serviceAccountName" -}}
{{- if .Values.controlPanel.serviceAccount.create }}
{{- default (include "config-poc.fullname" .) .Values.controlPanel.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.controlPanel.serviceAccount.name }}
{{- end }}
{{- end }}
