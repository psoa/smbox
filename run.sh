#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_FILE="$SCRIPT_DIR/smbox.pid"

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if kill -0 "$PID" 2>/dev/null; then
    echo "smbox is already running (PID $PID). Stop it first."
    exit 1
  fi
  rm -f "$PID_FILE"
fi

nohup java -jar "$SCRIPT_DIR/smbox.jar" > "$SCRIPT_DIR/smbox.log" 2>&1 &
echo $! > "$PID_FILE"
echo "smbox started (PID $(cat "$PID_FILE"))"