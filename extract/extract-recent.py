import sys, json
import pyodbc

from datetime import datetime


SERVER = sys.argv[1]
DATABASE = 'bbr'
USERNAME = sys.argv[2]
PASSWORD = sys.argv[3]

BASE_OUTPUT_PATH = '/Users/timsawyer/web/bbr-contest-data-export'

print (f'Connecting to {SERVER} as {USERNAME}')

connectionString = f'DRIVER={{ODBC Driver 18 for SQL Server}};SERVER={SERVER};DATABASE={DATABASE};UID={USERNAME};PWD={PASSWORD}'
QUERY_RECENT_RESULTS = "SELECT DISTINCT r.contest_event_id FROM contest_result r WHERE r.updated > CONVERT(datetime, '%s' ) ORDER BY 1 ASC" % ('2023-11-24')
QUERY_CONTEST_EVENT_DETAILS = """SELECT * FROM contest_event e 
    INNER JOIN contest contest ON contest.id = e.contest_id 
    INNER JOIN contest_type contest_type ON contest_type.id = e.contest_type_id
    INNER JOIN contest_type default_contest_type ON default_contest_type.id = contest.default_contest_type_id
    LEFT OUTER JOIN contest_group contest_group ON contest_group.id = contest.contest_group_id
    LEFT OUTER JOIN venue venue ON venue.id = e.venue_id
    LEFT OUTER JOIN region region ON region.id = contest.region_id
    LEFT OUTER JOIN section section ON section.id = contest.section_id
    WHERE e.id = %d FOR JSON AUTO, INCLUDE_NULL_VALUES"""  

conn = pyodbc.connect(connectionString) 
cursor = conn.cursor()
cursor.execute(QUERY_RECENT_RESULTS)
records = cursor.fetchall()
for r in records:
    cursor = conn.cursor()
    cursor.execute(QUERY_CONTEST_EVENT_DETAILS % r.contest_event_id)
    records = cursor.fetchall()
    for e in records:
        print(e)
        parsedDataList = json.loads(e[0])
        parsedData = parsedDataList[0]
        eventDateRaw = parsedData['date_of_event']
        eventDate = datetime.strptime(eventDateRaw, '%Y-%m-%d')
        contest = parsedData['contest'][0]
        slug = contest['slug']
        year = eventDate.year
        filename = f'{eventDateRaw}_{slug}.json'
        filePath = '%s/%d/%s/%s/%s' % (BASE_OUTPUT_PATH, year, slug, eventDateRaw, filename)
        print(filePath)
        print(parsedData)    