import psycopg2
import sys

def check_db():
    conn = None
    try:
        conn = psycopg2.connect(
            dbname="restaurant",
            user="postgres",
            password="1234",
            host="localhost"
        )
        cur = conn.cursor()
        
        print("--- Flyway Schema History ---")
        cur.execute("SELECT version, description, type, script, checksum, installed_on, execution_time, success FROM flyway_schema_history ORDER BY installed_rank DESC;")
        rows = cur.fetchall()
        for row in rows:
            print(row)
            
        print("\n--- Orders Table Columns ---")
        cur.execute("SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'orders' ORDER BY column_name;")
        rows = cur.fetchall()
        for row in rows:
            print(row)
            
        cur.close()
    except Exception as e:
        print(f"Error: {e}")
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    check_db()
