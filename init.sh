#!/bin/sh
echo "=========== INITIALIZING LOCALSTACK RESOURCES ==========="

# 1. Create the custom Event Bus
awslocal events create-event-bus --name app-event-bus

echo "=========== LOCALSTACK SETUP COMPLETE ==========="