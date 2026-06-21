#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_FILE="$SCRIPT_DIR/smbox.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "smbox is not running."
  exit 0
fi

PID=$(cat "$PID_FILE")
if kill -0 "$PID" 2>/dev/null; then
  echo "Stopping smbox (PID $PID)..."
  kill "$PID" && rm -f "$PID_FILE"
  echo "smbox stopped."
else
  echo "smbox is not running (stale PID file removed)."
  rm -f "$PID_FILE"
fi