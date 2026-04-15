from docx import Document
from docx.shared import Pt, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

doc = Document()

for section in doc.sections:
    section.top_margin    = Cm(2.0)
    section.bottom_margin = Cm(2.0)
    section.left_margin   = Cm(2.0)
    section.right_margin  = Cm(2.0)

def add_heading(doc, text, level=1):
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.bold = True
    if level == 0:
        run.font.size = Pt(16)
        run.font.color.rgb = RGBColor(0x1F, 0x39, 0x64)
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after  = Pt(10)
    elif level == 1:
        run.font.size = Pt(13)
        run.font.color.rgb = RGBColor(0x1F, 0x39, 0x64)
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after  = Pt(6)
    return p

def shade_cell(cell, hex_color):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    shd = OxmlElement('w:shd')
    shd.set(qn('w:val'),   'clear')
    shd.set(qn('w:color'), 'auto')
    shd.set(qn('w:fill'),  hex_color)
    tcPr.append(shd)

def add_table(doc, headers, rows, col_widths):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER

    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = h
        shade_cell(cell, '1F3964')
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        run = p.runs[0]
        run.font.bold  = True
        run.font.size  = Pt(9)
        run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)

    for ri, row_data in enumerate(rows):
        fill = 'FFFFFF' if ri % 2 == 0 else 'EBF3FB'
        row = table.rows[ri + 1]
        for ci, val in enumerate(row_data):
            cell = row.cells[ci]
            shade_cell(cell, fill)
            p = cell.paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            run = p.add_run(val)
            run.font.size = Pt(9)
            if ci == len(headers) - 1:
                if val == 'Pass':
                    run.font.color.rgb = RGBColor(0x17, 0x84, 0x46)
                    run.font.bold = True
                elif val == 'Fail':
                    run.font.color.rgb = RGBColor(0xC0, 0x00, 0x00)
                    run.font.bold = True

    for i, w in enumerate(col_widths):
        for r in table.rows:
            r.cells[i].width = Cm(w)

    doc.add_paragraph()
    return table

# Title
add_heading(doc, 'FitTrack - Software Testing Documentation', 0)

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('BCA 6th Semester Final Year Project  |  Tribhuvan University  |  2026')
run.font.size   = Pt(10)
run.font.italic = True
run.font.color.rgb = RGBColor(0x44, 0x44, 0x44)
p.paragraph_format.space_after = Pt(4)

# SECTION 1 - UNIT TESTING
add_heading(doc, '1. Unit Testing', 1)

unit_headers = ['Test Case ID', 'Module', 'Input', 'Expected Output', 'Status']

unit_rows = [
    ['UT-01', 'BmrCalculator',      'Male, 25 yrs, 70 kg, 175 cm',             'BMR = 1731.25 kcal',                          'Pass'],
    ['UT-02', 'BmrCalculator',      'Female, 30 yrs, 60 kg, 163 cm',           'BMR = 1382.75 kcal',                          'Pass'],
    ['UT-03', 'BmrCalculator',      'Null weight / height',                     'Returns default 1800 kcal',                   'Pass'],
    ['UT-04', 'MacroCalculator',    'Goal=WEIGHT_LOSS, TDEE=2200 kcal',        'Daily goal = 1700 kcal',                      'Pass'],
    ['UT-05', 'MacroCalculator',    'Goal=MUSCLE_GAIN, goalKcal=2500 kcal',    'Protein=219g, Carbs=281g, Fat=56g',           'Pass'],
    ['UT-06', 'BmrCalculator',      'ActivityLevel = GYM_GOING',               'Multiplier = 1.55',                           'Pass'],
    ['UT-07', 'DailyRecommendation','3 consecutive WorkoutSet days logged',     'isRestDay = true',                            'Pass'],
    ['UT-08', 'DailyRecommendation','gymToday=true, cardioToday=false',         'ExercisePlan = Cardio - Brisk Walk 30 min',   'Pass'],
    ['UT-09', 'NepaliMealDatabase', 'Slot=BREAKFAST, Goal=WEIGHT_LOSS',        'Returns Chiura + Dahi (320 kcal)',            'Pass'],
    ['UT-10', 'SafetyTipGenerator', 'medicalConditions = [DIABETES]',          'Tip includes blood glucose check reminder',   'Pass'],
]

add_table(doc, unit_headers, unit_rows, col_widths=[2.0, 3.5, 4.8, 5.5, 1.6])

# SECTION 2 - SYSTEM TESTING
add_heading(doc, '2. System Testing', 1)

sys_headers = ['Test Case ID', 'Scenario', 'Steps', 'Expected Result', 'Status']

sys_rows = [
    ['ST-01', 'User Registration',
     '1. Open /register\n2. Enter name, email, password\n3. Submit',
     'Account created; redirected to login page',
     'Pass'],
    ['ST-02', 'User Login',
     '1. Open /login\n2. Enter valid credentials\n3. Submit',
     'JWT issued; dashboard loads successfully',
     'Pass'],
    ['ST-03', 'Invalid Login',
     '1. Open /login\n2. Enter wrong password\n3. Submit',
     'HTTP 401 returned; error message shown',
     'Pass'],
    ['ST-04', 'User Profile Setup',
     '1. Login\n2. Go to Profile\n3. Enter age, weight, height, goal\n4. Save',
     'Profile saved; BMR & TDEE auto-calculated and displayed',
     'Pass'],
    ['ST-05', 'Log Gym Session',
     '1. Login\n2. Go to Workout\n3. Add exercise with sets/reps/weight\n4. Save',
     'WorkoutSet saved; calories burned shown on dashboard',
     'Pass'],
    ['ST-06', 'Log Food Entry',
     '1. Login\n2. Go to Nutrition\n3. Select food item\n4. Add with quantity',
     'FoodEntry saved; daily calorie counter updates',
     'Pass'],
    ['ST-07', 'View Daily Recommendations',
     "1. Login\n2. Go to Recommendations\n3. Click Get Today's Plan",
     'Exercise plan, meal suggestions, safety tips displayed',
     'Pass'],
    ['ST-08', 'Rest Day Detection',
     '1. Login\n2. Log gym sessions 3 days in a row\n3. Open Recommendations',
     'System recommends Rest Day; no strength training suggested',
     'Pass'],
    ['ST-09', 'Calorie Summary',
     '1. Login\n2. Log food and workout\n3. Open Dashboard',
     'Calories consumed, burned, deficit/surplus displayed correctly',
     'Pass'],
    ['ST-10', 'Logout',
     '1. Login\n2. Click Logout',
     'JWT cleared; redirected to login; protected routes blocked',
     'Pass'],
]

add_table(doc, sys_headers, sys_rows, col_widths=[2.0, 3.0, 5.5, 5.0, 1.6])

out = r'E:\FInalProject\fitness\FitTrack_Testing_Documentation.docx'
doc.save(out)
print('Saved: ' + out)
