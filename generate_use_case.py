"""
Generates use_case_diagram.xml (Draw.io / mxGraph format)
for the FitTrack fitness web application.
"""

lines = []

def esc(v):
    # Escape XML attribute value: &, ", and literal newlines
    return v.replace('&', '&amp;').replace('"', '&quot;').replace('\n', '&#xa;')

def xml(tag, attrs, close=False, text=""):
    parts = [f'<{tag}']
    for k, v in attrs.items():
        parts.append(f' {k}="{esc(v)}"')
    if close and not text:
        parts.append('/>')
    elif text:
        parts.append(f'>{text}</{tag}>')
    else:
        parts.append('>')
    lines.append(''.join(parts))

def end(tag):
    lines.append(f'</{tag}>')

# ── helpers ───────────────────────────────────────────────────────────────────
_id = 10

def nid():
    global _id
    _id += 1
    return str(_id)

ACTOR_STYLE = (
    "shape=mxgraph.uml2.actor;"
    "fillColor=#dae8fc;strokeColor=#6c8ebf;"
    "fontStyle=1;fontSize=11;"
)

UC_STYLE = (
    "ellipse;whiteSpace=wrap;html=1;"
    "fillColor=#fff2cc;strokeColor=#d6b656;"
    "fontSize=10;align=center;verticalAlign=middle;"
)

BOX_STYLE = (
    "rounded=1;arcSize=4;whiteSpace=wrap;html=1;"
    "fillColor=#f5f5f5;strokeColor=#666666;"
    "fontStyle=1;fontSize=11;verticalAlign=top;"
    "fontColor=#333333;"
)

SYS_STYLE = (
    "rounded=1;arcSize=2;whiteSpace=wrap;html=1;"
    "fillColor=none;strokeColor=#1F3964;"
    "fontStyle=1;fontSize=14;verticalAlign=top;"
    "fontColor=#1F3964;strokeWidth=3;"
)

ASSOC_STYLE   = "endArrow=none;html=1;exitX=1;exitY=0.5;exitDX=0;exitDY=0;"
INCL_STYLE    = "dashed=1;endArrow=open;endFill=0;html=1;fontSize=10;"
EXTEND_STYLE  = "dashed=1;endArrow=open;endFill=0;html=1;fontSize=10;endArrow=openThin;"
GEN_STYLE     = "endArrow=block;endFill=0;html=1;exitX=0.5;exitY=0;exitDX=0;exitDY=0;"

def actor(aid, label, x, y, w=60, h=90):
    xml('mxCell', {
        'id': aid, 'value': label, 'vertex': '1', 'parent': '1',
        'style': ACTOR_STYLE,
    })
    xml('mxGeometry', {'x': str(x), 'y': str(y), 'width': str(w), 'height': str(h), 'as': 'geometry'}, close=True)
    end('mxCell')

def usecase(uid, label, x, y, w=160, h=50):
    xml('mxCell', {
        'id': uid, 'value': label, 'vertex': '1', 'parent': '1',
        'style': UC_STYLE,
    })
    xml('mxGeometry', {'x': str(x), 'y': str(y), 'width': str(w), 'height': str(h), 'as': 'geometry'}, close=True)
    end('mxCell')

def box(bid, label, x, y, w, h):
    xml('mxCell', {
        'id': bid, 'value': label, 'vertex': '1', 'parent': '1',
        'style': BOX_STYLE,
    })
    xml('mxGeometry', {'x': str(x), 'y': str(y), 'width': str(w), 'height': str(h), 'as': 'geometry'}, close=True)
    end('mxCell')

def sysbox(bid, label, x, y, w, h):
    xml('mxCell', {
        'id': bid, 'value': label, 'vertex': '1', 'parent': '1',
        'style': SYS_STYLE,
    })
    xml('mxGeometry', {'x': str(x), 'y': str(y), 'width': str(w), 'height': str(h), 'as': 'geometry'}, close=True)
    end('mxCell')

def edge(src, tgt, label="", style=ASSOC_STYLE):
    eid = nid()
    xml('mxCell', {
        'id': eid, 'value': label, 'edge': '1',
        'source': src, 'target': tgt, 'parent': '1',
        'style': style,
    })
    xml('mxGeometry', {'relative': '1', 'as': 'geometry'}, close=True)
    end('mxCell')

def incl(src, tgt):
    edge(src, tgt, '&lt;&lt;include&gt;&gt;', INCL_STYLE)

def ext(src, tgt):
    edge(src, tgt, '&lt;&lt;extend&gt;&gt;', EXTEND_STYLE)

# ══════════════════════════════════════════════════════════════════════════════
#  BUILD DIAGRAM
# ══════════════════════════════════════════════════════════════════════════════

lines.append('<?xml version="1.0" encoding="UTF-8"?>')
lines.append('<mxGraphModel dx="1422" dy="762" grid="1" gridSize="10" guides="1" '
             'tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" '
             'pageWidth="3500" pageHeight="2600" math="0" shadow="0">')
lines.append('<root>')
lines.append('<mxCell id="0"/>')
lines.append('<mxCell id="1" parent="0"/>')

# ── ACTORS ────────────────────────────────────────────────────────────────────
GUEST   = 'a1'
USER    = 'a2'
DB      = 'a3'
JWT     = 'a4'
ADMIN   = 'a5'

actor(GUEST, 'Guest User',       60,  380,  70, 100)
actor(USER,  'Registered User',  60,  900,  70, 100)
actor(DB,    'MySQL Database',  3300,  700,  70, 100)
actor(JWT,   'JWT Auth\nService',3300, 1100,  70, 100)

# ── SYSTEM BOUNDARY ───────────────────────────────────────────────────────────
sysbox('sys', 'FitTrack – Fitness Web Application', 200, 60, 3050, 2450)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 1 – AUTHENTICATION
# ─────────────────────────────────────────────────────────────────────────────
box('m1', 'Authentication Module', 240, 100, 530, 260)

UC_REG   = 'u1'
UC_LOGIN = 'u2'
UC_LOGOUT= 'u3'
UC_JWT   = 'u4'

usecase(UC_REG,    'Register Account',      260,  160, 160, 50)
usecase(UC_LOGIN,  'Login',                 260,  230, 160, 50)
usecase(UC_LOGOUT, 'Logout',                450,  160, 160, 50)
usecase(UC_JWT,    'Validate JWT Token',    450,  230, 160, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 2 – USER PROFILE
# ─────────────────────────────────────────────────────────────────────────────
box('m2', 'User Profile Module', 800, 100, 380, 200)

UC_SAVE_PROF = 'u5'
UC_GET_PROF  = 'u6'

usecase(UC_SAVE_PROF, 'Setup / Update Profile', 820, 160, 170, 50)
usecase(UC_GET_PROF,  'View Profile',            820, 225, 170, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 3 – WORKOUT
# ─────────────────────────────────────────────────────────────────────────────
box('m3', 'Workout Module', 240, 395, 600, 490)

UC_LOG_SET    = 'u7'
UC_TODAY_SETS = 'u8'
UC_ALL_SETS   = 'u9'
UC_MUSCLE_SETS= 'u10'
UC_EX_PROG    = 'u11'
UC_LOG_CARDIO = 'u12'
UC_TODAY_CARD = 'u13'
UC_ALL_CARD   = 'u14'

usecase(UC_LOG_SET,     'Log Gym Set',              260,  455, 155, 50)
usecase(UC_TODAY_SETS,  "View Today's Gym Sets",    260,  520, 155, 50)
usecase(UC_ALL_SETS,    'View Gym History',         260,  585, 155, 50)
usecase(UC_MUSCLE_SETS, 'View Sets by\nMuscle Group', 260, 650, 155, 50)
usecase(UC_EX_PROG,     'View Exercise Progress',  260,  720, 155, 50)
usecase(UC_LOG_CARDIO,  'Log Cardio Session',      445,  455, 170, 50)
usecase(UC_TODAY_CARD,  "View Today's Cardio",     445,  520, 170, 50)
usecase(UC_ALL_CARD,    'View Cardio History',     445,  585, 170, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 4 – NUTRITION
# ─────────────────────────────────────────────────────────────────────────────
box('m4', 'Nutrition Module', 870, 395, 380, 370)

UC_LOG_FOOD  = 'u15'
UC_TODAY_FOOD= 'u16'
UC_ALL_FOOD  = 'u17'
UC_DEL_FOOD  = 'u18'

usecase(UC_LOG_FOOD,   'Log Food Entry',          890,  455, 165, 50)
usecase(UC_TODAY_FOOD, "View Today's Food",       890,  520, 165, 50)
usecase(UC_ALL_FOOD,   'View Food History',       890,  585, 165, 50)
usecase(UC_DEL_FOOD,   'Delete Food Entry',       890,  650, 165, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 5 – ACTIVITY
# ─────────────────────────────────────────────────────────────────────────────
box('m5', 'Activity Module', 1210, 100, 340, 200)

UC_TRACK_ACT = 'u19'
UC_VIEW_ACT  = 'u20'

usecase(UC_TRACK_ACT, 'Track Activity',   1225, 160, 155, 50)
usecase(UC_VIEW_ACT,  'View Activities',  1225, 225, 155, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 6 – RECOMMENDATION
# ─────────────────────────────────────────────────────────────────────────────
box('m6', 'Recommendation Module', 1280, 395, 460, 370)

UC_GEN_REC    = 'u21'
UC_DAILY_REC  = 'u22'
UC_USER_REC   = 'u23'
UC_ACT_REC    = 'u24'
UC_BMR_CALC   = 'u25'

usecase(UC_GEN_REC,   'Generate\nRecommendation',    1300, 455, 170, 50)
usecase(UC_DAILY_REC, 'View Daily\nRecommendation',  1300, 520, 170, 50)
usecase(UC_USER_REC,  'View User\nRecommendations',  1300, 585, 170, 50)
usecase(UC_ACT_REC,   'View Activity\nRecommendation',1300,650, 170, 50)
usecase(UC_BMR_CALC,  'Calculate BMR / TDEE',        1500, 520, 200, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 7 – CALORIE SUMMARY
# ─────────────────────────────────────────────────────────────────────────────
box('m7', 'Calorie Summary Module', 1580, 100, 380, 200)

UC_CAL_SUM = 'u26'
usecase(UC_CAL_SUM, 'View Calorie Summary\n& Macro Targets', 1600, 160, 200, 65)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 8 – DASHBOARD
# ─────────────────────────────────────────────────────────────────────────────
box('m8', 'Dashboard Module', 1990, 100, 340, 200)

UC_DASHBOARD = 'u27'
usecase(UC_DASHBOARD, 'View Dashboard', 2005, 160, 165, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 9 – PROGRESS
# ─────────────────────────────────────────────────────────────────────────────
box('m9', 'Progress Module', 240, 920, 600, 420)

UC_LOG_WT    = 'u28'
UC_VIEW_WT   = 'u29'
UC_LOG_MEAS  = 'u30'
UC_VIEW_MEAS = 'u31'
UC_WEEKLY    = 'u32'

usecase(UC_LOG_WT,    'Log Weight',             260,  980, 160, 50)
usecase(UC_VIEW_WT,   'View Weight History',    260, 1050, 160, 50)
usecase(UC_LOG_MEAS,  'Log Body Measurements', 260, 1120, 160, 50)
usecase(UC_VIEW_MEAS, 'View Measurements',     260, 1190, 160, 50)
usecase(UC_WEEKLY,    'View Weekly Progress',  450,  980, 165, 50)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 10 – NEXT DAY PLAN
# ─────────────────────────────────────────────────────────────────────────────
box('m10', 'Next Day Plan Module', 870, 920, 380, 200)

UC_NEXT_PLAN = 'u33'
usecase(UC_NEXT_PLAN, 'View Next Day Plan\n(Exercise + Meals)', 890, 980, 200, 65)

# ─────────────────────────────────────────────────────────────────────────────
#  MODULE 11 – SECURITY (internal)
# ─────────────────────────────────────────────────────────────────────────────
box('m11', 'Security', 1580, 395, 380, 200)

UC_AUTH_GUARD = 'u34'
UC_ISSUE_JWT  = 'u35'

usecase(UC_AUTH_GUARD, 'Authenticate Request\n(JWT Guard)',  1600, 455, 170, 50)
usecase(UC_ISSUE_JWT,  'Issue JWT Token',                   1600, 520, 170, 50)

# ══════════════════════════════════════════════════════════════════════════════
#  ASSOCIATIONS – Guest → Auth
# ══════════════════════════════════════════════════════════════════════════════
edge(GUEST, UC_REG)
edge(GUEST, UC_LOGIN)

# ── Registered User → all authenticated use cases ────────────────────────────
auth_uc = [
    UC_LOGOUT, UC_SAVE_PROF, UC_GET_PROF,
    UC_LOG_SET, UC_TODAY_SETS, UC_ALL_SETS, UC_MUSCLE_SETS, UC_EX_PROG,
    UC_LOG_CARDIO, UC_TODAY_CARD, UC_ALL_CARD,
    UC_LOG_FOOD, UC_TODAY_FOOD, UC_ALL_FOOD, UC_DEL_FOOD,
    UC_TRACK_ACT, UC_VIEW_ACT,
    UC_GEN_REC, UC_DAILY_REC, UC_USER_REC, UC_ACT_REC,
    UC_CAL_SUM, UC_DASHBOARD,
    UC_LOG_WT, UC_VIEW_WT, UC_LOG_MEAS, UC_VIEW_MEAS, UC_WEEKLY,
    UC_NEXT_PLAN,
]
for uc in auth_uc:
    edge(USER, uc)

# ── External system associations ──────────────────────────────────────────────
edge(DB, UC_REG)
edge(DB, UC_LOGIN)
edge(DB, UC_LOG_SET)
edge(DB, UC_LOG_FOOD)
edge(DB, UC_LOG_WT)
edge(DB, UC_LOG_MEAS)
edge(JWT, UC_ISSUE_JWT)
edge(JWT, UC_AUTH_GUARD)

# ══════════════════════════════════════════════════════════════════════════════
#  <<include>> RELATIONSHIPS
# ══════════════════════════════════════════════════════════════════════════════
# Login includes: Validate JWT + Issue JWT
incl(UC_LOGIN, UC_ISSUE_JWT)
incl(UC_LOGIN, UC_AUTH_GUARD)

# All protected calls include auth guard
incl(UC_SAVE_PROF,   UC_AUTH_GUARD)
incl(UC_GEN_REC,     UC_AUTH_GUARD)
incl(UC_CAL_SUM,     UC_AUTH_GUARD)
incl(UC_DASHBOARD,   UC_AUTH_GUARD)
incl(UC_NEXT_PLAN,   UC_AUTH_GUARD)

# Recommendation includes BMR/TDEE calc
incl(UC_GEN_REC,   UC_BMR_CALC)
incl(UC_CAL_SUM,   UC_BMR_CALC)
incl(UC_NEXT_PLAN, UC_BMR_CALC)

# Daily recommendation includes generate recommendation
incl(UC_DAILY_REC,  UC_GEN_REC)
incl(UC_NEXT_PLAN,  UC_GEN_REC)

# Dashboard includes calorie summary
incl(UC_DASHBOARD, UC_CAL_SUM)

# Weekly progress includes weight + measurement history
incl(UC_WEEKLY, UC_VIEW_WT)
incl(UC_WEEKLY, UC_VIEW_MEAS)

# ══════════════════════════════════════════════════════════════════════════════
#  <<extend>> RELATIONSHIPS
# ══════════════════════════════════════════════════════════════════════════════
# View Activity Recommendation extends Generate Recommendation
ext(UC_ACT_REC, UC_GEN_REC)

# View User Recommendations extends View Daily Recommendation
ext(UC_USER_REC, UC_DAILY_REC)

# Delete Food extends Log Food (post-log action)
ext(UC_DEL_FOOD, UC_LOG_FOOD)

# ══════════════════════════════════════════════════════════════════════════════
#  CLOSE
# ══════════════════════════════════════════════════════════════════════════════
lines.append('</root>')
lines.append('</mxGraphModel>')

out = r'E:\FInalProject\fitness\use_case_diagram.xml'
with open(out, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))
print('Saved:', out)
